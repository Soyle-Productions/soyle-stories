package com.soyle.stories.theme.updateThemeMetaData

import com.soyle.stories.common.Notifier
import com.soyle.stories.theme.usecases.updateThemeMetaData.RenamedTheme
import kotlin.coroutines.coroutineContext

class RenamedThemeNotifier : RenamedThemeReceiver, Notifier<RenamedThemeReceiver>() {
    override suspend fun receiveRenamedTheme(renamedTheme: RenamedTheme) {
        notifyAll(coroutineContext) { it.receiveRenamedTheme(renamedTheme) }
    }
}