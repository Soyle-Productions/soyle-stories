package com.soyle.stories.theme.deleteTheme

import com.soyle.stories.common.Notifier
import com.soyle.stories.usecase.theme.deleteTheme.DeletedTheme

class ThemeDeletedNotifier : Notifier<ThemeDeletedReceiver>(), ThemeDeletedReceiver {
    override suspend fun receiveDeletedTheme(deletedTheme: DeletedTheme) {
        notifyAll { it.receiveDeletedTheme(deletedTheme) }
    }
}