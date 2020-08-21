package com.soyle.stories.theme.useCharacterAsMainOpponent

import com.soyle.stories.common.Notifier
import com.soyle.stories.theme.usecases.useCharacterAsMainOpponent.CharacterUsedAsMainOpponent
import kotlin.coroutines.coroutineContext

class CharacterUsedAsMainOpponentNotifier : CharacterUsedAsMainOpponentReceiver, Notifier<CharacterUsedAsMainOpponentReceiver>() {

    override suspend fun receiveCharacterUsedAsMainOpponent(characterUsedAsMainOpponent: CharacterUsedAsMainOpponent) {
        notifyAll { it.receiveCharacterUsedAsMainOpponent(characterUsedAsMainOpponent) }
    }
}