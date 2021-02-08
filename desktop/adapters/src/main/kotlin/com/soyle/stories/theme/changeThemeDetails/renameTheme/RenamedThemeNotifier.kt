package com.soyle.stories.theme.changeThemeDetails.renameTheme

import com.soyle.stories.common.Notifier
import com.soyle.stories.usecase.theme.changeThemeDetails.RenamedTheme

class RenamedThemeNotifier : RenamedThemeReceiver, Notifier<RenamedThemeReceiver>() {
    override suspend fun receiveRenamedTheme(renamedTheme: RenamedTheme) {
        notifyAll { it.receiveRenamedTheme(renamedTheme) }
    }
}