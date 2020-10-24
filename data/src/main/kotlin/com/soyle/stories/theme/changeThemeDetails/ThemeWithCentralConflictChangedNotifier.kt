package com.soyle.stories.theme.changeThemeDetails

import com.soyle.stories.common.Notifier
import com.soyle.stories.theme.usecases.changeThemeDetails.CentralConflictChanged

class ThemeWithCentralConflictChangedNotifier : ThemeWithCentralConflictChangedReceiver,
    Notifier<ThemeWithCentralConflictChangedReceiver>() {
    override suspend fun receiveThemeWithCentralConflictChanged(centralConflictChanged: CentralConflictChanged) {
        notifyAll { it.receiveThemeWithCentralConflictChanged(centralConflictChanged) }
    }
}