package com.soyle.stories.theme.useCharacterAsOpponent

import com.soyle.stories.common.Notifier
import com.soyle.stories.theme.usecases.useCharacterAsOpponent.CharacterUsedAsOpponent
import kotlin.coroutines.coroutineContext

class CharacterUsedAsOpponentNotifier : CharacterUsedAsOpponentReceiver, Notifier<CharacterUsedAsOpponentReceiver>() {

    override suspend fun receiveCharacterUsedAsOpponent(characterUsedAsOpponent: CharacterUsedAsOpponent) {
        notifyAll(coroutineContext) { it.receiveCharacterUsedAsOpponent(characterUsedAsOpponent) }
    }

}