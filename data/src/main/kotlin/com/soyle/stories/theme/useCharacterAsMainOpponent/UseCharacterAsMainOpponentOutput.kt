package com.soyle.stories.theme.useCharacterAsMainOpponent

import com.soyle.stories.theme.useCharacterAsOpponent.OpponentCharacterReceiver
import com.soyle.stories.theme.usecases.useCharacterAsMainOpponent.UseCharacterAsMainOpponent

class UseCharacterAsMainOpponentOutput(
    private val opponentCharacterNotifier: OpponentCharacterReceiver
) : UseCharacterAsMainOpponent.OutputPort {

    override suspend fun characterUsedAsMainOpponent(response: UseCharacterAsMainOpponent.ResponseModel) {
        response.previousMainOpponent?.let { opponentCharacterNotifier.receiveOpponentCharacter(it) }
        opponentCharacterNotifier.receiveOpponentCharacter(response.mainOpponent)
    }

}