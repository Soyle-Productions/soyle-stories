package com.soyle.stories.character.removeCharacterFromStory

import com.soyle.stories.usecase.character.removeCharacterFromStory.RemoveCharacterFromStory
import com.soyle.stories.common.Notifier

class RemoveCharacterConfirmationNotifier : Notifier<RemoveCharacterConfirmationReceiver>(), RemoveCharacterConfirmationReceiver {
    override suspend fun receiveRemoveCharacterConfirmationRequest(request: RemoveCharacterFromStory.ConfirmationRequest) {
        notifyAll { it.receiveRemoveCharacterConfirmationRequest(request) }
    }
}