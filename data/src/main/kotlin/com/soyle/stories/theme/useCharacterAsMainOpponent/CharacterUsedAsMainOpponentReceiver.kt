package com.soyle.stories.theme.useCharacterAsMainOpponent

import com.soyle.stories.theme.usecases.useCharacterAsMainOpponent.CharacterUsedAsMainOpponent

interface CharacterUsedAsMainOpponentReceiver {

    suspend fun receiveCharacterUsedAsMainOpponent(characterUsedAsMainOpponent: CharacterUsedAsMainOpponent)

}