package com.soyle.stories.character.removeCharacterFromStory

import com.soyle.stories.common.Receiver
import com.soyle.stories.domain.character.events.CharacterRemovedFromStory
import com.soyle.stories.usecase.character.remove.RemoveCharacterFromStory
import com.soyle.stories.theme.removeCharacterFromComparison.RemovedCharacterFromThemeReceiver

class RemoveCharacterFromStoryOutput(
    private val characterRemovedReceiver: Receiver<CharacterRemovedFromStory>,
) : RemoveCharacterFromStory.OutputPort {

    override suspend fun characterRemovedFromProject(response: RemoveCharacterFromStory.ResponseModel) {
        characterRemovedReceiver.receiveEvent(response.characterRemoved)
    }

}