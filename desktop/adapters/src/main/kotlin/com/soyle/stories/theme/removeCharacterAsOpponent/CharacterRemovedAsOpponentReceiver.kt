package com.soyle.stories.theme.removeCharacterAsOpponent

import com.soyle.stories.theme.usecases.removeCharacterAsOpponent.CharacterRemovedAsOpponent

interface CharacterRemovedAsOpponentReceiver {

    suspend fun receiveCharacterRemovedAsOpponent(characterRemovedAsOpponent: CharacterRemovedAsOpponent)

}