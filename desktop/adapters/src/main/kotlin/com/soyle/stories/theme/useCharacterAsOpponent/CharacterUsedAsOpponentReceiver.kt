package com.soyle.stories.theme.useCharacterAsOpponent

import com.soyle.stories.usecase.theme.useCharacterAsOpponent.CharacterUsedAsOpponent

interface CharacterUsedAsOpponentReceiver {

    suspend fun receiveCharacterUsedAsOpponent(characterUsedAsOpponent: CharacterUsedAsOpponent)
}