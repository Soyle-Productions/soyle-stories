package com.soyle.stories.theme.useCharacterAsMainOpponent

import com.soyle.stories.theme.useCharacterAsOpponent.CharacterUsedAsOpponentReceiver
import com.soyle.stories.theme.usecases.useCharacterAsMainOpponent.CharacterUsedAsMainOpponent
import com.soyle.stories.theme.usecases.useCharacterAsMainOpponent.UseCharacterAsMainOpponent

class UseCharacterAsMainOpponentOutput(
    private val characterUsedAsMainOpponentReceiver: CharacterUsedAsMainOpponentReceiver,
    private val characterUsedAsOpponentReceiver: CharacterUsedAsOpponentReceiver
) : UseCharacterAsMainOpponent.OutputPort {

    override suspend fun characterUsedAsMainOpponent(response: UseCharacterAsMainOpponent.ResponseModel) {
        characterUsedAsMainOpponentReceiver.receiveCharacterUsedAsMainOpponent(response.mainOpponent)
        response.previousMainOpponent?.let { characterUsedAsOpponentReceiver.receiveCharacterUsedAsOpponent(it) }
    }

}