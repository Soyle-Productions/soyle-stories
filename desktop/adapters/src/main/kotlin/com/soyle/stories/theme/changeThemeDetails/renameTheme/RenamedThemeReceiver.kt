package com.soyle.stories.theme.changeThemeDetails.renameTheme

import com.soyle.stories.usecase.theme.changeThemeDetails.RenamedTheme

interface RenamedThemeReceiver {
    suspend fun receiveRenamedTheme(renamedTheme: RenamedTheme)
}