package com.soyle.stories.theme.changeThemeDetails.changeCentralConflict

import com.soyle.stories.usecase.theme.changeThemeDetails.CentralConflictChanged

interface CentralConflictChangedReceiver {
    suspend fun receiveThemeWithCentralConflictChanged(centralConflictChanged: CentralConflictChanged)
}