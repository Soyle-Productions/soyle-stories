package com.soyle.stories.theme.removeCharacterAsOpponent

import com.soyle.stories.common.Notifier
import com.soyle.stories.usecase.theme.removeCharacterAsOpponent.CharacterRemovedAsOpponent

class CharacterRemovedAsOpponentNotifier : CharacterRemovedAsOpponentReceiver, Notifier<CharacterRemovedAsOpponentReceiver>() {

    override suspend fun receiveCharacterRemovedAsOpponent(characterRemovedAsOpponent: CharacterRemovedAsOpponent) {
        notifyAll { it.receiveCharacterRemovedAsOpponent(characterRemovedAsOpponent) }
    }
}