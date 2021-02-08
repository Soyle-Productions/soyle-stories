package com.soyle.stories.theme.useCharacterAsMainOpponent

import com.soyle.stories.usecase.theme.useCharacterAsOpponent.CharacterUsedAsMainOpponent

interface CharacterUsedAsMainOpponentReceiver {

    suspend fun receiveCharacterUsedAsMainOpponent(characterUsedAsMainOpponent: CharacterUsedAsMainOpponent)

}