package com.soyle.stories.desktop.config.drivers.scene

import com.soyle.stories.desktop.view.prose.proseEditor.ProseEditorDriver
import com.soyle.stories.desktop.view.prose.proseEditor.ProseEditorDriver.Companion.drive
import com.soyle.stories.desktop.view.prose.proseEditor.ProseEditorDriver.Companion.driver
import com.soyle.stories.desktop.view.scene.sceneEditor.SceneEditorDriver.Companion.driver
import com.soyle.stories.desktop.view.scene.sceneList.SceneListDriver
import com.soyle.stories.desktop.view.type
import com.soyle.stories.entities.Scene
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.prose.proseEditor.ContentElement
import com.soyle.stories.scene.sceneEditor.SceneEditorScope
import com.soyle.stories.scene.sceneEditor.SceneEditorView
import com.soyle.stories.scene.sceneList.SceneList
import javafx.scene.input.KeyCode
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import tornadofx.FX

fun SceneList.givenSceneEditorToolHasBeenOpened(scene: Scene): SceneEditorView =
    scope.getSceneEditorTool(scene) ?: openSceneEditorTool(scene).let { scope.getSceneEditorToolOrError(scene) }

fun ProjectScope.getSceneEditorToolOrError(scene: Scene): SceneEditorView =
    getSceneEditorTool(scene) ?: error("No Scene Editor open for scene ${scene.name}")

fun ProjectScope.getSceneEditorTool(scene: Scene): SceneEditorView? {
    return toolScopes.asSequence()
        .filterIsInstance<SceneEditorScope>()
        .find { it.type.sceneId == scene.id }
        ?.let { FX.getComponents(it)[SceneEditorView::class] as? SceneEditorView }
}

fun SceneList.openSceneEditorTool(scene: Scene) {
    val driver = SceneListDriver(this)
    val item = driver.getSceneItemOrError(scene.name.value)
    driver.interact {
        driver.getTree().selectionModel.select(item)
        with(driver) {
            item.getSceneEditorItem().fire()
        }
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

private fun ProseEditorDriver.getOffsetForMention(mentionName: String): Pair<Int, ContentElement>?
{
    var offset = 0
    val mention = textArea.paragraphs.asSequence().flatMap { it.segments.asSequence() }
        .find { segment ->
            (segment.text == mentionName).also {
                if (!it) offset += segment.text.length
            }
        }
    return mention?.let { offset to mention }
}
fun SceneEditorView.atRightOfMention(mentionName: String): SceneEditorView
{
    driver().getProseEditor()
        .drive {
            val (offset, mention) = getOffsetForMention(mentionName)!!
            textArea.moveTo(offset + mention.text.length)
        }
    return this
}

fun SceneEditorView.atLeftOfMention(mentionName: String): SceneEditorView
{
    driver().getProseEditor()
        .drive {
            val (offset, mention) = getOffsetForMention(mentionName)!!
            textArea.moveTo(offset)
        }
    return this
}
fun SceneEditorView.typeKey(keyCode: KeyCode): SceneEditorView
{
    driver().getProseEditor()
        .drive {
            textArea.requestFocus()
            type(keyCode)
        }
    runBlocking { delay(100) }
    driver().getProseEditor().drive { textArea.parent.requestFocus() }
    return this
}
fun SceneEditorView.query(query: String)
{
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
fun SceneEditorView.givenStoryElementsQueried(query: String)
{
    val driver = driver().getProseEditor().driver()
    if (! driver.isShowingMentionMenu()) {
        query(query)
    }
}
fun SceneEditorView.selectMentionSuggestion(suggestionName: String)
{
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
fun SceneEditorView.selectMentionSuggestionAndUse(suggestionName: String)
{
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