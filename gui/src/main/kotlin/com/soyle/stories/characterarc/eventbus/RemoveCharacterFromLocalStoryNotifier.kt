package com.soyle.stories.characterarc.eventbus

import com.soyle.stories.character.CharacterException
import com.soyle.stories.character.usecases.removeCharacterFromLocalStory.RemoveCharacterFromLocalStory
import com.soyle.stories.eventbus.Notifier

class RemoveCharacterFromLocalStoryNotifier : RemoveCharacterFromLocalStory.OutputPort, Notifier<RemoveCharacterFromLocalStory.OutputPort>() {
    override fun receiveRemoveCharacterFromLocalStoryFailure(failure: CharacterException) {
        notifyAll { it.receiveRemoveCharacterFromLocalStoryFailure(failure) }
    }

    override fun receiveRemoveCharacterFromLocalStoryResponse(response: RemoveCharacterFromLocalStory.ResponseModel) {
        notifyAll { it.receiveRemoveCharacterFromLocalStoryResponse(response) }
    }
}