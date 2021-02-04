package com.soyle.stories.character.removeCharacterFromStory

import com.soyle.stories.character.usecases.removeCharacterFromStory.RemoveCharacterFromStory

interface RemoveCharacterConfirmationReceiver {
    suspend fun receiveRemoveCharacterConfirmationRequest(request: RemoveCharacterFromStory.ConfirmationRequest)
}