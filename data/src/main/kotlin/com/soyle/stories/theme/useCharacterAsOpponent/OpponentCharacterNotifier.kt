package com.soyle.stories.theme.useCharacterAsOpponent

import com.soyle.stories.common.Notifier
import com.soyle.stories.theme.usecases.useCharacterAsOpponent.OpponentCharacter
import kotlin.coroutines.coroutineContext

class OpponentCharacterNotifier : OpponentCharacterReceiver, Notifier<OpponentCharacterReceiver>() {

    override suspend fun receiveOpponentCharacter(opponentCharacter: OpponentCharacter) {
        notifyAll(coroutineContext) { it.receiveOpponentCharacter(opponentCharacter) }
    }

}