package com.soyle.stories.theme.useCharacterAsOpponent

import com.soyle.stories.theme.usecases.useCharacterAsOpponent.OpponentCharacter

interface OpponentCharacterReceiver {

    suspend fun receiveOpponentCharacter(opponentCharacter: OpponentCharacter)
}