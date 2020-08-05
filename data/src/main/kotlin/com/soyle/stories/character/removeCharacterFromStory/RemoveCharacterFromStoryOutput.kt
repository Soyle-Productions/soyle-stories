package com.soyle.stories.character.removeCharacterFromStory

import com.soyle.stories.character.usecases.removeCharacterFromStory.RemoveCharacterFromStory
import com.soyle.stories.theme.removeCharacterFromComparison.RemovedCharacterFromThemeReceiver

class RemoveCharacterFromStoryOutput(
    private val characterRemovedReceiver: RemovedCharacterReceiver,
    private val removedCharacterFromThemeReceiver: RemovedCharacterFromThemeReceiver
) : RemoveCharacterFromStory.OutputPort {

    override suspend fun receiveRemoveCharacterFromStoryResponse(response: RemoveCharacterFromStory.ResponseModel) {
        characterRemovedReceiver.receiveCharacterRemoved(response.removedCharacter)
        response.removedCharacterFromThemes.forEach {
            removedCharacterFromThemeReceiver.receiveRemovedCharacterFromTheme(it)
        }
    }

}