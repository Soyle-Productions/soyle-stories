package com.soyle.stories.theme.changeThemeDetails

import com.soyle.stories.common.Notifier
import com.soyle.stories.theme.usecases.changeThemeDetails.RenamedTheme

class RenamedThemeNotifier : RenamedThemeReceiver, Notifier<RenamedThemeReceiver>() {
    override suspend fun receiveRenamedTheme(renamedTheme: RenamedTheme) {
        notifyAll { it.receiveRenamedTheme(renamedTheme) }
    }
}