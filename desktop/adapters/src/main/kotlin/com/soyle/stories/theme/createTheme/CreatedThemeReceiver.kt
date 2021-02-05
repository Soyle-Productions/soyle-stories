package com.soyle.stories.theme.createTheme

import com.soyle.stories.theme.usecases.createTheme.CreatedTheme

interface CreatedThemeReceiver {
    suspend fun receiveCreatedTheme(createdTheme: CreatedTheme)
}