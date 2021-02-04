package com.soyle.stories.theme.createTheme

import com.soyle.stories.common.Notifier
import com.soyle.stories.theme.usecases.createTheme.CreatedTheme
import kotlin.coroutines.coroutineContext

class CreatedThemeNotifier : Notifier<CreatedThemeReceiver>(), CreatedThemeReceiver {
    override suspend fun receiveCreatedTheme(createdTheme: CreatedTheme) {
        notifyAll { it.receiveCreatedTheme(createdTheme) }
    }
}