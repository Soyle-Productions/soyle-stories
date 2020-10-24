package com.soyle.stories.theme.changeThemeDetails

import com.soyle.stories.theme.usecases.changeThemeDetails.RenamedTheme

interface RenamedThemeReceiver {
    suspend fun receiveRenamedTheme(renamedTheme: RenamedTheme)
}