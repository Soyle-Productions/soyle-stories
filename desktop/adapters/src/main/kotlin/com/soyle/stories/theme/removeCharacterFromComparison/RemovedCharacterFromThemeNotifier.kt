package com.soyle.stories.theme.removeCharacterFromComparison

import com.soyle.stories.common.Notifier
import com.soyle.stories.usecase.theme.removeCharacterFromComparison.RemovedCharacterFromTheme

class RemovedCharacterFromThemeNotifier : RemovedCharacterFromThemeReceiver, Notifier<RemovedCharacterFromThemeReceiver>() {

    override suspend fun receiveRemovedCharacterFromTheme(removedCharacterFromTheme: RemovedCharacterFromTheme) {
        notifyAll { it.receiveRemovedCharacterFromTheme(removedCharacterFromTheme) }
    }

}