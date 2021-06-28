package com.soyle.stories.desktop.config.drivers.scene

import com.soyle.stories.characterarc.createCharacterDialog.CreateCharacterForm
import com.soyle.stories.desktop.config.drivers.character.getCreateCharacterDialogOrError
import com.soyle.stories.desktop.config.drivers.location.getCreateLocationDialogOrError
import com.soyle.stories.desktop.config.drivers.theme.getCreateSymbolDialogOrError
import com.soyle.stories.desktop.view.prose.proseEditor.ProseEditorDriver
import com.soyle.stories.desktop.view.prose.proseEditor.ProseEditorDriver.Companion.drive
import com.soyle.stories.desktop.view.prose.proseEditor.ProseEditorDriver.Companion.driver
import com.soyle.stories.desktop.view.scene.sceneEditor.SceneEditorDriver.Companion.drive
import com.soyle.stories.desktop.view.scene.sceneEditor.SceneEditorDriver.Companion.driver
import com.soyle.stories.desktop.view.scene.sceneList.SceneListDriver
import com.soyle.stories.desktop.view.type
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.location.createLocationDialog.CreateLocationDialog
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.prose.proseEditor.ContentElement
import com.soyle.stories.prose.proseEditor.MentionIssueMenu
import com.soyle.stories.scene.sceneEditor.SceneEditorScope
import com.soyle.stories.scene.sceneEditor.SceneEditorView
import com.soyle.stories.scene.sceneList.SceneListView
import com.soyle.stories.theme.createSymbolDialog.CreateSymbolDialog
import javafx.event.EventType
import javafx.scene.input.ContextMenuEvent
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.PickResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import tornadofx.FX

fun SceneListView.givenSceneEditorToolHasBeenOpened(scene: Scene): SceneEditorView =
    scope.getSceneEditorTool(scene) ?: openSceneEditorTool(scene).let { scope.getSceneEditorToolOrError(scene) }

fun ProjectScope.getSceneEditorToolOrError(scene: Scene): SceneEditorView =
    getSceneEditorTool(scene) ?: error("No Scene Editor open for scene ${scene.name}")

fun ProjectScope.getSceneEditorTool(scene: Scene): SceneEditorView? {
    return toolScopes.asSequence()
        .filterIsInstance<SceneEditorScope>()
        .find { it.type.sceneId == scene.id }
        ?.let { FX.getComponents(it)[SceneEditorView::class] as? SceneEditorView }
}

fun SceneListView.openSceneEditorTool(scene: Scene) {
    val driver = SceneListDriver(this)
    val item = driver.getSceneItemOrError(scene.name.value)
    driver.interact {
        driver.tree.selectionModel.select(item)
        with(driver) {
            item.getSceneEditorItem().fire()
        }
    }
}

fun SceneEditorView.setConflict(conflict: String) {
    drive {
        conflictFieldInput.requestFocus()
        conflictFieldInput.text = conflict
        resolutionFieldInput.requestFocus()
    }
}
fun SceneEditorView.setResolution(resolution: String) {
    drive {
        resolutionFieldInput.requestFocus()
        resolutionFieldInput.text = resolution
        conflictFieldInput.requestFocus()
    }
}

fun SceneEditorView.requestStoryElementsMatching(query: String) {
    val keyCodes = query.asSequence()
        .map(Character::toString)
        .map(String::toUpperCase)
        .map(KeyCode::getKeyCode)
        .toList()
        .toTypedArray()
    driver().getProseEditor()
        .drive {
            textArea.requestFocus()
            press(KeyCode.SHIFT).type(KeyCode.DIGIT2).release(KeyCode.SHIFT)
            type(*keyCodes)
        }
}

fun SceneEditorView.enterText(text: String) {
    driver().getProseEditor()
        .drive {

            textArea.requestFocus()

            type(text)
        }
    runBlocking { delay(1500) }
    driver().getProseEditor()
        .drive {
            textArea.parent.requestFocus()
        }
}

private fun ProseEditorDriver.getOffsetForMention(mentionName: String): Pair<Int, ContentElement>? {
    var offset = 0
    val mention = textArea.paragraphs.asSequence().flatMap { it.segments.asSequence() }
        .find { segment ->
            (segment.text == mentionName).also {
                if (!it) offset += segment.text.length
            }
        }
    return mention?.let { offset to mention }
}

fun SceneEditorView.atRightOfMention(mentionName: String): SceneEditorView {
    driver().getProseEditor()
        .drive {
            val (offset, mention) = getOffsetForMention(mentionName)!!
            textArea.moveTo(offset + mention.text.length)
        }
    return this
}

fun SceneEditorView.atLeftOfMention(mentionName: String): SceneEditorView {
    driver().getProseEditor()
        .drive {
            val (offset, mention) = getOffsetForMention(mentionName)!!
            textArea.moveTo(offset)
        }
    return this
}

fun SceneEditorView.typeKey(keyCode: KeyCode): SceneEditorView {
    driver().getProseEditor()
        .drive {
            textArea.requestFocus()
            type(keyCode)
        }
    runBlocking { delay(100) }
    driver().getProseEditor().drive { textArea.parent.requestFocus() }
    return this
}

fun SceneEditorView.query(query: String) {
    driver().getProseEditor()
        .drive {
            textArea.requestFocus()
            press(KeyCode.SHIFT).type(KeyCode.DIGIT2).release(KeyCode.SHIFT)
        }
    runBlocking { delay(100) }
    driver().getProseEditor()
        .drive {
            type(query)
        }
    runBlocking { delay(100) }
}

fun SceneEditorView.givenStoryElementsQueried(query: String) {
    val driver = driver().getProseEditor().driver()
    if (!driver.isShowingMentionMenu()) {
        query(query)
    }
}

fun SceneEditorView.selectMentionSuggestion(suggestionName: String) {
    driver().getProseEditor()
        .drive {
            val mentionItem = mentionMenuItems.find { it.name.toString() == suggestionName }
                ?: error("could not find mention with name $suggestionName in $mentionMenuItems")
            mentionMenuList.selectionModel.select(mentionItem)
            type(KeyCode.ENTER)
        }
    runBlocking { delay(100) }
    driver().getProseEditor()
        .drive {
            textArea.parent.requestFocus()
        }
}

fun SceneEditorView.selectMentionSuggestionAndUse(suggestionName: String) {
    driver().getProseEditor()
        .drive {
            val mentionItem = mentionMenuItems.find { it.name.toString() == suggestionName }
                ?: error("could not find mention with name $suggestionName in $mentionMenuItems")
            mentionMenuList.selectionModel.select(mentionItem)
            press(KeyCode.SHIFT).type(KeyCode.ENTER).release(KeyCode.SHIFT)
        }
    runBlocking { delay(100) }
    driver().getProseEditor()
        .drive {
            textArea.parent.requestFocus()
        }
}

fun SceneEditorView.givenMentionIsBeingInvestigated(mentionName: String): SceneEditorView {
    if (!isMentionBeingInvestigated(mentionName)) investigateMention(mentionName)

    return this
}

fun SceneEditorView.isMentionBeingInvestigated(mentionName: String): Boolean = driver().getProseEditor().driver().run {
    isShowingMentionIssueMenu() && mentionIssueMenuIsRelatedToMention(mentionName)
}

fun SceneEditorView.investigateMention(mentionName: String): MentionIssueMenu? {
    return driver().getProseEditor()
        .drive {
            textArea.requestFocus()
            textArea.moveTo(textArea.text.indexOf(mentionName) + 1)
            textArea.onContextMenuRequested.handle(
                ContextMenuEvent(
                    ContextMenuEvent.CONTEXT_MENU_REQUESTED,
                    -1.0,
                    -1.0,
                    -1.0,
                    -1.0,
                    true,
                    PickResult(textArea, 0.0, 0.0)
                )
            )
        }.mentionIssueMenu?.takeIf { it.isShowing }
}

fun SceneEditorView.clearAllMentionsOfEntity() {
    val clearMentionOption = with(driver().getProseEditor().driver()) {
        mentionIssueMenu!!.clearMentionOption()!!
    }
    driver().interact {
        clearMentionOption.fire()
    }
}

fun SceneEditorView.selectReplacementSuggestion(suggestionText: String)
{
    val replacementOption = with(driver().getProseEditor().driver()) {
        mentionIssueMenu!!.replacementOption()!!
    }
    val replacementSuggestion = replacementOption.items.find { it.text == suggestionText } ?: error("No replacement suggestion available named $suggestionText")
    driver().interact {
        replacementSuggestion.fire()
    }
}

fun SceneEditorView.givenReplacingInvestigatedMentionWithNewCharacter(): CreateCharacterForm
{
    val replacementOption = with(driver().getProseEditor().driver()) {
        mentionIssueMenu!!.replacementOption()!!
    }
    driver().interact {
        replacementOption.items.first().fire()
    }
    return getCreateCharacterDialogOrError()
}

fun SceneEditorView.givenReplacingInvestigatedMentionWithNewLocation(): CreateLocationDialog
{
    val replacementOption = with(driver().getProseEditor().driver()) {
        mentionIssueMenu!!.replacementOption()!!
    }
    driver().interact {
        replacementOption.items.first().fire()
    }
    return getCreateLocationDialogOrError()
}

fun SceneEditorView.givenReplacingInvestigatedMentionWithNewSymbol(): CreateSymbolDialog
{
    val replacementOption = with(driver().getProseEditor().driver()) {
        mentionIssueMenu!!.replacementOption()!!
    }
    driver().interact {
        replacementOption.items.first().fire()
    }
    return getCreateSymbolDialogOrError()
}
