package com.soyle.stories.desktop.config.drivers.scene

import com.soyle.stories.character.create.CreateCharacterDialog
import com.soyle.stories.characterarc.createArcSectionDialog.CreateArcSectionDialogView
import com.soyle.stories.common.ViewOf
import com.soyle.stories.desktop.config.drivers.awaitOrContinue
import com.soyle.stories.desktop.config.drivers.awaitWithTimeout
import com.soyle.stories.desktop.config.drivers.character.getCreateArcSectionDialog
import com.soyle.stories.desktop.config.drivers.character.getCreateArcSectionDialogOrError
import com.soyle.stories.desktop.config.drivers.character.getCreateCharacterDialog
import com.soyle.stories.desktop.config.drivers.character.getCreateCharacterDialogOrError
import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.desktop.config.drivers.scene.character.getOpenStoryEventPrompt
import com.soyle.stories.desktop.config.drivers.soylestories.findMenuItemById
import com.soyle.stories.desktop.view.project.workbench.getOpenDialog
import com.soyle.stories.desktop.view.scene.sceneCharacters.`Scene Characters Access`.Companion.access
import com.soyle.stories.desktop.view.scene.sceneCharacters.`Scene Characters Access`.Companion.drive
import com.soyle.stories.di.get
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.project.WorkBench
import com.soyle.stories.scene.characters.include.selectCharacter.CharacterSuggestion
import com.soyle.stories.scene.characters.inspect.CharacterInSceneInspectionViewModel
import com.soyle.stories.scene.characters.remove.ConfirmRemoveCharacterFromScenePromptView
import com.soyle.stories.scene.characters.tool.SceneCharactersToolComponent
import com.soyle.stories.scene.characters.tool.SceneCharactersToolViewModel
import impl.org.controlsfx.skin.AutoCompletePopup
import javafx.scene.control.ButtonBase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.javafx.awaitPulse
import kotlinx.coroutines.withContext
import tornadofx.FX

fun WorkBench.givenSceneCharactersToolHasBeenOpened(): SceneCharactersToolComponent =
    getOpenSceneCharactersTool() ?: run {
        openSceneCharactersTool()
        getOpenSceneCharactersToolOrError()
    }

fun WorkBench.getOpenSceneCharactersToolOrError(scene: Scene? = null): SceneCharactersToolComponent =
    getOpenSceneCharactersTool(scene) ?: error("No Scene Characters tool is open in the project")

fun WorkBench.getOpenSceneCharactersTool(scene: Scene? = null): SceneCharactersToolComponent? {
    return (FX.getComponents(scope)[SceneCharactersToolComponent::class] as? SceneCharactersToolComponent)?.takeIf { it.currentWindow?.isShowing == true }
    //    ?.takeIf { scene == null || (it.viewModel.sceneSelection.value as? SceneCharactersToolViewModel.SceneSelection.Selected)?.sceneId == scene.id }
}

fun WorkBench.openSceneCharactersTool(scene: Scene? = null) {
    findMenuItemById("tools_scene characters")!!.apply {
            robot.interact { fire() }
        }
    awaitWithTimeout(1000) { getOpenSceneCharactersTool(scene) != null }
}

fun SceneCharactersToolComponent.givenFocusedOn(scene: Scene): SceneCharactersToolComponent {
    if (!access().isFocusedOn(scene)) focusOn(scene)
    return this
}

fun SceneCharactersToolComponent.focusOn(scene: Scene) {
    scope.get<WorkBench>().givenSceneListToolHasBeenOpened().selectScene(scene)
    awaitWithTimeout(1000) { access().isFocusedOn(scene) }
}

fun SceneCharactersToolComponent.givenIncludingCharacter(): SceneCharactersToolComponent {
    if (access().includeCharacterPopup?.isShowing != true) includeCharacter()
    return this
}

fun SceneCharactersToolComponent.includeCharacter() {
    drive {
        includeCharacterSelection!!.fire()
    }
    awaitWithTimeout(1000) { drive { includeCharacterPopup?.isShowing == true } }
}

fun SceneCharactersToolComponent.givenCreatingNewCharacter(): CreateCharacterDialog {
    if (getCreateCharacterDialog() == null) createNewCharacter()
    return getCreateCharacterDialogOrError()
}

fun SceneCharactersToolComponent.createNewCharacter() {
    givenIncludingCharacter()
    drive {
        createNewCharacterItem.fireEvent(AutoCompletePopup.SuggestionEvent(CharacterSuggestion(null)))
    }
    awaitWithTimeout(1000) { getCreateCharacterDialog() != null }
}

fun SceneCharactersToolComponent.givenCharacterChosen(character: Character): SceneCharactersToolComponent {
    if (getOpenStoryEventPrompt() == null) chooseCharacter(character)
    awaitWithTimeout(1000) { getOpenStoryEventPrompt() != null }
    return this
}

fun SceneCharactersToolComponent.chooseCharacter(character: Character) {
    drive {
        clickOn(getAvailableCharacterItem(character)!!)
    }
}

fun SceneCharactersToolComponent.givenStoryEventSelected(storyEvent: StoryEvent): SceneCharactersToolComponent {
    if (access().includeCharacterPopup?.isShowing != true || access().availableStoryEventItems.isNotEmpty()) {
        selectStoryEvent(storyEvent)
    }
    return this
}

fun SceneCharactersToolComponent.selectStoryEvent(storyEvent: StoryEvent) {
    drive {
        clickOn(getAvailableStoryEventItem(storyEvent.id)!!)
    }
}

fun SceneCharactersToolComponent.givenInspecting(characterId: Character.Id): ViewOf<CharacterInSceneInspectionViewModel> {
    if (!access().isInspecting(characterId)) {
        inspectCharacter(characterId)
        awaitWithTimeout(1000) {
            drive { isInspecting(characterId) }
        }
    }
    return access().characterInspection!!
}

fun SceneCharactersToolComponent.givenRemovingCharacter(character: Character) {
    givenInspecting(character.id)
    drive {
        characterInspection!!.removeButton.fire()
    }
}

fun SceneCharactersToolComponent.openConfirmRemoveCharacterFromScenePrompt(characterId: Character.Id) {
    givenInspecting(characterId)
    drive {
        characterInspection!!.removeButton.fire()
    }
    awaitOrContinue(1000) {
        robot.getOpenDialog<ConfirmRemoveCharacterFromScenePromptView> { it.viewModel.isNeeded } != null
    }
}

fun SceneCharactersToolComponent.removeCharacter(character: Character) {
    givenInspecting(character.id)
    drive {
        characterInspection!!.removeButton.fire()
    }
}

fun SceneCharactersToolComponent.givenAvailableArcsToCoverHaveBeenRequestedFor(character: Character): SceneCharactersToolComponent {
    givenInspecting(character.id)
    val arcRequested = drive {
        with(characterInspection!!) {
            haveAvailableArcsToCoverBeenRequested()
        }
    }
    if (!arcRequested) requestAvailableArcsToCoverFor(character)
    return this
}

private fun SceneCharactersToolComponent.inspectCharacter(characterId: Character.Id) {
    drive {
        getCharacterItem(characterId)!!.editButton.fire()
    }
}

fun SceneCharactersToolComponent.requestAvailableArcsToCoverFor(character: Character) {
    givenInspecting(character.id)
    drive {
        characterInspection!!.positionOnArcSelection.show()
    }
}

fun SceneCharactersToolComponent.coverSectionInArc(arcName: String, sectionLabel: String) {
    drive {
        characterInspection!!.positionOnArcSelection.show()
        characterInspection!!.positionOnArcSelection.getSectionItemOrError(arcName, sectionLabel).fire()
    }
}

fun SceneCharactersToolComponent.uncoverSectionInArc(arcName: String, sectionLabel: String) {
    drive {
        characterInspection!!.positionOnArcSelection.show()
        characterInspection!!.positionOnArcSelection.getSectionItemOrError(arcName, sectionLabel).fire()
    }
}

fun SceneCharactersToolComponent.givenCreateNewSectionInArcSelected(arcName: String): CreateArcSectionDialogView {
    val dialog = getCreateArcSectionDialog()
    if (dialog == null) {
        drive {
            characterInspection!!.positionOnArcSelection.show()
            characterInspection!!.positionOnArcSelection.getCreateArcSectionOptionOrError(arcName).fire()
        }
        return getCreateArcSectionDialogOrError()
    } else return dialog
}

fun SceneCharactersToolComponent.setDesireAs(desire: String) {
    drive {
        with(characterInspection!!.desireInput) {
            requestFocus()
            text = desire
            parent.requestFocus()
        }
    }
}

suspend fun SceneCharactersToolComponent.setMotivationAs(motivation: String) {
    with(access()) {
        withContext(Dispatchers.JavaFx) {
            with(characterInspection!!.motivationInput) {
                requestFocus()
                text = motivation
                parent.requestFocus()
            }
        }
        awaitPulse()
    }
}

fun SceneCharactersToolComponent.assignRole(role: String) {
    drive {
        val roleToggle: ButtonBase = when (role) {
            "Inciting Character" -> characterInspection!!.incitingCharacterToggle
            else -> characterInspection!!.opponentCharacterToggle
        }
        roleToggle.fire()
    }
}