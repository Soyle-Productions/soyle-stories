package com.soyle.stories.theme.changeCharacterChange

import com.soyle.stories.common.Notifier
import com.soyle.stories.theme.usecases.changeCharacterChange.ChangedCharacterChange
import kotlin.coroutines.coroutineContext

class ChangedCharacterChangeNotifier : ChangedCharacterChangeReceiver, Notifier<ChangedCharacterChangeReceiver>() {

    override suspend fun receiveChangedCharacterChange(changedCharacterChange: ChangedCharacterChange) {
        notifyAll(coroutineContext) { it.receiveChangedCharacterChange(changedCharacterChange) }
    }
}