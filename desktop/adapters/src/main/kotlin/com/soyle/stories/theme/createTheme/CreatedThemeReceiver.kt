package com.soyle.stories.theme.createTheme

import com.soyle.stories.usecase.theme.createTheme.CreatedTheme

interface CreatedThemeReceiver {
    suspend fun receiveCreatedTheme(createdTheme: CreatedTheme)
}