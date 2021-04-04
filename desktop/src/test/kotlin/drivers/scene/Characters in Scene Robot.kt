package com.soyle.stories.desktop.config.drivers.scene

import com.soyle.stories.characterarc.createArcSectionDialog.CreateArcSectionDialogView
import com.soyle.stories.desktop.config.drivers.character.getCreateArcSectionDialog
import com.soyle.stories.desktop.config.drivers.character.getCreateArcSectionDialogOrError
import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.desktop.config.drivers.soylestories.findMenuItemById
import com.soyle.stories.desktop.view.scene.sceneCharacters.drive
import com.soyle.stories.desktop.view.scene.sceneCharacters.driver
import com.soyle.stories.di.get
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.project.WorkBench
import com.soyle.stories.scene.sceneCharacters.SceneCharactersView
import tornadofx.FX


fun WorkBench.givenSceneCharactersToolHasBeenOpened(): SceneCharactersView =
    getOpenSceneCharactersTool() ?: openSceneCharactersTool().run { getOpenSceneCharactersToolOrError() }

fun WorkBench.getOpenSceneCharactersToolOrError(): SceneCharactersView =
    getOpenSceneCharactersTool() ?: error("No Scene Characters tool is open in the project")

fun WorkBench.getOpenSceneCharactersTool(): SceneCharactersView? {
    return (FX.getComponents(scope)[SceneCharactersView::class] as? SceneCharactersView)?.takeIf { it.currentWindow?.isShowing == true }
}

fun WorkBench.openSceneCharactersTool() {
    findMenuItemById("tools_scene characters")!!
        .apply { robot.interact { fire() } }
}

fun SceneCharactersView.givenFocusedOn(scene: Scene): SceneCharactersView {
    if (!driver().isFocusedOn(scene)) focusOn(scene)
    return this
}

fun SceneCharactersView.focusOn(scene: Scene) {
    scope.get<WorkBench>().givenSceneListToolHasBeenOpened()
        .selectScene(scene)
}

fun SceneCharactersView.includeCharacter(character: Character) {
    drive {
        includeCharacterSelection.show()
        getAvailableCharacterItem(character)!!.fire()
    }
}

fun SceneCharactersView.givenEditing(character: Character): SceneCharactersView
{
    if (!driver().isEditing(character.id)) editCharacter(character)
    return this
}

fun SceneCharactersView.removeCharacter(character: Character)
{
    givenEditing(character)
    drive {
        getCharacterEditorOrError().removeButton.fire()
    }
}

fun SceneCharactersView.givenAvailableArcsToCoverHaveBeenRequestedFor(character: Character): SceneCharactersView {
    givenEditing(character)
    val arcRequested = drive {
        with(getCharacterEditorOrError()) {
            haveAvailableArcsToCoverBeenRequested()
        }
    }
    if (!arcRequested) requestAvailableArcsToCoverFor(character)
    return this
}

private fun SceneCharactersView.editCharacter(character: Character) {
    drive {
        getCharacterItemOrError(character.id)
            .editButton.fire()
    }
}

fun SceneCharactersView.requestAvailableArcsToCoverFor(character: Character) {
    givenEditing(character)
    drive {
        getCharacterEditorOrError().positionOnArcSelection.show()
    }
}

fun SceneCharactersView.coverSectionInArc(arcName: String, sectionLabel: String) {
    drive {
        getCharacterEditorOrError().positionOnArcSelection.show()
        getCharacterEditorOrError().positionOnArcSelection.getSectionItemOrError(arcName, sectionLabel).fire()
    }
}

fun SceneCharactersView.uncoverSectionInArc(arcName: String, sectionLabel: String) {
    drive {
        getCharacterEditorOrError().positionOnArcSelection.show()
        getCharacterEditorOrError().positionOnArcSelection.getSectionItemOrError(arcName, sectionLabel).fire()
    }
}

fun SceneCharactersView.givenCreateNewSectionInArcSelected(arcName: String): CreateArcSectionDialogView {
    val dialog = getCreateArcSectionDialog()
    if (dialog == null) {
        drive {
            getCharacterEditorOrError().positionOnArcSelection.show()
            getCharacterEditorOrError().positionOnArcSelection.getCreateArcSectionOptionOrError(arcName).fire()
        }
        return getCreateArcSectionDialogOrError()
    }
    else return dialog
}

fun SceneCharactersView.setMotivationAs(motivation: String)
{
    drive {
        with(getCharacterEditorOrError().motivationInput) {
            requestFocus()
            text = motivation
            parent.requestFocus()
        }
    }
}