package com.soyle.stories.theme.updateThemeMetaData

import com.soyle.stories.common.Notifier
import com.soyle.stories.theme.usecases.updateThemeMetaData.ThemeWithCentralConflictChanged
import kotlin.coroutines.coroutineContext

class ThemeWithCentralConflictChangedNotifier : ThemeWithCentralConflictChangedReceiver,
    Notifier<ThemeWithCentralConflictChangedReceiver>() {
    override suspend fun receiveThemeWithCentralConflictChanged(themeWithCentralConflictChanged: ThemeWithCentralConflictChanged) {
        notifyAll(coroutineContext) { it.receiveThemeWithCentralConflictChanged(themeWithCentralConflictChanged) }
    }
}