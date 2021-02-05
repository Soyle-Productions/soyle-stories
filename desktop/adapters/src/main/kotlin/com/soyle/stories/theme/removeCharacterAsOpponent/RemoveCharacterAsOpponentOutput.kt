package com.soyle.stories.theme.removeCharacterAsOpponent

import com.soyle.stories.theme.usecases.removeCharacterAsOpponent.RemoveCharacterAsOpponent

class RemoveCharacterAsOpponentOutput(
    private val characterRemovedAsOpponentReceiver: CharacterRemovedAsOpponentReceiver
) : RemoveCharacterAsOpponent.OutputPort {

    override suspend fun removedCharacterAsOpponent(response: RemoveCharacterAsOpponent.ResponseModel) {
        characterRemovedAsOpponentReceiver.receiveCharacterRemovedAsOpponent(response.characterRemovedAsOpponent)
    }

}