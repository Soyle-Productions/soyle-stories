package com.soyle.stories.character.removeCharacterFromStory

import com.soyle.stories.usecase.character.removeCharacterFromStory.RemoveCharacterFromStory

interface RemoveCharacterConfirmationReceiver {
    suspend fun receiveRemoveCharacterConfirmationRequest(request: RemoveCharacterFromStory.ConfirmationRequest)
}