package com.soyle.stories.theme.updateThemeMetaData

import com.soyle.stories.theme.usecases.updateThemeMetaData.RenamedTheme

interface RenamedThemeReceiver {
    suspend fun receiveRenamedTheme(renamedTheme: RenamedTheme)
}