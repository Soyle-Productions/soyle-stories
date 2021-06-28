package com.soyle.stories.theme.useCharacterAsMainOpponent

import com.soyle.stories.common.Notifier
import com.soyle.stories.usecase.theme.useCharacterAsOpponent.CharacterUsedAsMainOpponent

class CharacterUsedAsMainOpponentNotifier : CharacterUsedAsMainOpponentReceiver, Notifier<CharacterUsedAsMainOpponentReceiver>() {

    override suspend fun receiveCharacterUsedAsMainOpponent(characterUsedAsMainOpponent: CharacterUsedAsMainOpponent) {
        notifyAll { it.receiveCharacterUsedAsMainOpponent(characterUsedAsMainOpponent) }
    }
}