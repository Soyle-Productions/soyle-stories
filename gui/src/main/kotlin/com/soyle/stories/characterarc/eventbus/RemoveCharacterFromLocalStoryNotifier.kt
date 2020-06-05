package com.soyle.stories.characterarc.eventbus

import com.soyle.stories.character.usecases.removeCharacterFromStory.RemoveCharacterFromStory
import com.soyle.stories.common.Notifier

class RemoveCharacterFromLocalStoryNotifier : RemoveCharacterFromStory.OutputPort, Notifier<RemoveCharacterFromStory.OutputPort>() {

    override fun receiveRemoveCharacterFromStoryResponse(response: RemoveCharacterFromStory.ResponseModel) {
        notifyAll { it.receiveRemoveCharacterFromStoryResponse(response) }
    }

    override fun receiveRemoveCharacterFromStoryFailure(failure: Exception) {
        notifyAll { it.receiveRemoveCharacterFromStoryFailure(failure) }
    }

}