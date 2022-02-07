package com.soyle.stories.desktop.config.drivers.scene.character

import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.domain.character.Character
import com.soyle.stories.scene.characters.include.selectStoryEvent.SelectStoryEventPromptView
import com.soyle.stories.scene.characters.tool.SceneCharactersToolComponent
import tornadofx.ge
import tornadofx.uiComponent

fun SceneCharactersToolComponent.getOpenStoryEventPromptOrError(character: Character? = null): SelectStoryEventPromptView {
    return getOpenStoryEventPrompt(character) ?: error("Did not find open story event prompt")
}

fun getOpenStoryEventPrompt(character: Character? = null): SelectStoryEventPromptView? {
    return robot.listWindows().asSequence()
        .filter { it.isShowing }
        .mapNotNull { it.scene.root.uiComponent<SelectStoryEventPromptView>() }
        .filter { character == null || it.viewModel.characterName().get() == character.displayName.value }
        .singleOrNull()
}