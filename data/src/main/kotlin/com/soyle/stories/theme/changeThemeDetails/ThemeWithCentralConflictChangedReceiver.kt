package com.soyle.stories.theme.changeThemeDetails

import com.soyle.stories.theme.usecases.changeThemeDetails.CentralConflictChanged

interface ThemeWithCentralConflictChangedReceiver {
    suspend fun receiveThemeWithCentralConflictChanged(centralConflictChanged: CentralConflictChanged)
}