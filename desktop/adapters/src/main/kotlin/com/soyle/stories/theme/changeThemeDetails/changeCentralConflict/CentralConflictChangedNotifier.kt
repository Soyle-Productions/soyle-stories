package com.soyle.stories.theme.changeThemeDetails.changeCentralConflict

import com.soyle.stories.common.Notifier
import com.soyle.stories.usecase.theme.changeThemeDetails.CentralConflictChanged

class CentralConflictChangedNotifier : CentralConflictChangedReceiver,
    Notifier<CentralConflictChangedReceiver>() {
    override suspend fun receiveThemeWithCentralConflictChanged(centralConflictChanged: CentralConflictChanged) {
        notifyAll { it.receiveThemeWithCentralConflictChanged(centralConflictChanged) }
    }
}