package com.soyle.stories.characterarc.changeSectionValue

import com.soyle.stories.common.Notifier
import com.soyle.stories.theme.usecases.changeCharacterDesire.ChangedCharacterDesire
import kotlin.coroutines.coroutineContext

class ChangedCharacterDesireNotifier : ChangedCharacterDesireReceiver, Notifier<ChangedCharacterDesireReceiver>() {

    override suspend fun receiveChangedCharacterDesire(changedCharacterDesire: ChangedCharacterDesire) {
        notifyAll(coroutineContext) { it.receiveChangedCharacterDesire(changedCharacterDesire) }
    }
}