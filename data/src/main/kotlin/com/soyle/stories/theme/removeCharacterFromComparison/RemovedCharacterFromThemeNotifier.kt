package com.soyle.stories.theme.removeCharacterFromComparison

import com.soyle.stories.common.Notifier
import com.soyle.stories.theme.usecases.removeCharacterFromComparison.RemovedCharacterFromTheme
import kotlin.coroutines.coroutineContext

class RemovedCharacterFromThemeNotifier : RemovedCharacterFromThemeReceiver, Notifier<RemovedCharacterFromThemeReceiver>() {

    override suspend fun receiveRemovedCharacterFromTheme(removedCharacterFromTheme: RemovedCharacterFromTheme) {
        notifyAll { it.receiveRemovedCharacterFromTheme(removedCharacterFromTheme) }
    }

}