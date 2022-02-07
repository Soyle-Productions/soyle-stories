package com.soyle.stories.desktop.config.drivers.scene.character

import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.desktop.config.drivers.scene.openConfirmRemoveCharacterFromScenePrompt
import com.soyle.stories.desktop.view.project.workbench.getOpenDialog
import com.soyle.stories.domain.character.Character
import com.soyle.stories.scene.characters.remove.ConfirmRemoveCharacterFromScenePromptView
import com.soyle.stories.scene.characters.tool.SceneCharactersToolComponent
import com.soyle.stories.scene.characters.tool.SceneCharactersToolViewModel

fun SceneCharactersToolComponent.givenConfirmRemoveCharacterFromScenePromptHasBeenOpened(character: Character): ConfirmRemoveCharacterFromScenePromptView {
    return getOpenConfirmRemoveCharacterFromScenePrompt(
        (viewModel.sceneSelection.value as SceneCharactersToolViewModel.SceneSelection.Selected).sceneName.value,
        character.displayName.value
    ) ?: run {
        openConfirmRemoveCharacterFromScenePrompt(character.id)
        getOpenConfirmRemoveCharacterFromScenePromptOrError(
            (viewModel.sceneSelection.value as SceneCharactersToolViewModel.SceneSelection.Selected).sceneName.value,
            character.displayName.value
        )
    }
}

fun getOpenConfirmRemoveCharacterFromScenePrompt(
    sceneName: String,
    characterName: String
): ConfirmRemoveCharacterFromScenePromptView? {
    return robot.getOpenDialog {
        it.viewModel.sceneName == sceneName && it.viewModel.characterName == characterName
    }
}

fun getOpenConfirmRemoveCharacterFromScenePromptOrError(
    sceneName: String,
    characterName: String
): ConfirmRemoveCharacterFromScenePromptView =
    getOpenConfirmRemoveCharacterFromScenePrompt(sceneName, characterName) ?: error("Confirm Remove $characterName from $sceneName Prompt is not open")
