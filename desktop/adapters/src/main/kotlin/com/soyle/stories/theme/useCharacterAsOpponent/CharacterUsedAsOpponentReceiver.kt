package com.soyle.stories.theme.useCharacterAsOpponent

import com.soyle.stories.theme.usecases.useCharacterAsOpponent.CharacterUsedAsOpponent

interface CharacterUsedAsOpponentReceiver {

    suspend fun receiveCharacterUsedAsOpponent(characterUsedAsOpponent: CharacterUsedAsOpponent)
}