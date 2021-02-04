package com.soyle.stories.theme.changeThemeDetails.changeThemeLine

import com.soyle.stories.common.Notifier
import com.soyle.stories.theme.usecases.changeThemeDetails.ChangedThemeLine

class ChangedThemeLineNotifier : ChangedThemeLineReceiver, Notifier<ChangedThemeLineReceiver>() {

    override suspend fun receiveChangedThemeLine(event: ChangedThemeLine) {
        notifyAll { it.receiveChangedThemeLine(event) }
    }
}