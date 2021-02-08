package com.soyle.stories.theme.changeCharacterChange

import com.soyle.stories.common.Notifier
import com.soyle.stories.usecase.theme.changeCharacterChange.ChangedCharacterChange

class ChangedCharacterChangeNotifier : ChangedCharacterChangeReceiver, Notifier<ChangedCharacterChangeReceiver>() {

    override suspend fun receiveChangedCharacterChange(changedCharacterChange: ChangedCharacterChange) {
        notifyAll { it.receiveChangedCharacterChange(changedCharacterChange) }
    }
}