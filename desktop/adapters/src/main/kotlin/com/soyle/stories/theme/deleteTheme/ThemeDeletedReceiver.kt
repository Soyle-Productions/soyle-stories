package com.soyle.stories.theme.deleteTheme

import com.soyle.stories.usecase.theme.deleteTheme.DeletedTheme

interface ThemeDeletedReceiver {
    suspend fun receiveDeletedTheme(deletedTheme: DeletedTheme)
}