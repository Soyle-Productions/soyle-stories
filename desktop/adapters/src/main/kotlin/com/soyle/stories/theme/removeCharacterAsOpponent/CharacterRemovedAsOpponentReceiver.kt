package com.soyle.stories.theme.removeCharacterAsOpponent

import com.soyle.stories.usecase.theme.removeCharacterAsOpponent.CharacterRemovedAsOpponent

interface CharacterRemovedAsOpponentReceiver {

    suspend fun receiveCharacterRemovedAsOpponent(characterRemovedAsOpponent: CharacterRemovedAsOpponent)

}