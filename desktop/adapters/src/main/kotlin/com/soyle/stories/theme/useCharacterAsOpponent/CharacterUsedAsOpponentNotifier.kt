package com.soyle.stories.theme.useCharacterAsOpponent

import com.soyle.stories.common.Notifier
import com.soyle.stories.usecase.theme.useCharacterAsOpponent.CharacterUsedAsOpponent

class CharacterUsedAsOpponentNotifier : CharacterUsedAsOpponentReceiver, Notifier<CharacterUsedAsOpponentReceiver>() {

    override suspend fun receiveCharacterUsedAsOpponent(characterUsedAsOpponent: CharacterUsedAsOpponent) {
        notifyAll { it.receiveCharacterUsedAsOpponent(characterUsedAsOpponent) }
    }

}