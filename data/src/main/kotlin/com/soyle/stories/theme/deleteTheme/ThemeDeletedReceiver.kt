package com.soyle.stories.theme.deleteTheme

import com.soyle.stories.theme.usecases.deleteTheme.DeletedTheme

interface ThemeDeletedReceiver {
    suspend fun receiveDeletedTheme(deletedTheme: DeletedTheme)
}