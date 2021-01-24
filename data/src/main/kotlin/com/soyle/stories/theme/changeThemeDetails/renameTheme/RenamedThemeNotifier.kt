package com.soyle.stories.theme.changeThemeDetails.renameTheme

import com.soyle.stories.common.Notifier
import com.soyle.stories.theme.changeThemeDetails.renameTheme.RenamedThemeReceiver
import com.soyle.stories.theme.usecases.changeThemeDetails.RenamedTheme

class RenamedThemeNotifier : RenamedThemeReceiver, Notifier<RenamedThemeReceiver>() {
    override suspend fun receiveRenamedTheme(renamedTheme: RenamedTheme) {
        notifyAll { it.receiveRenamedTheme(renamedTheme) }
    }
}