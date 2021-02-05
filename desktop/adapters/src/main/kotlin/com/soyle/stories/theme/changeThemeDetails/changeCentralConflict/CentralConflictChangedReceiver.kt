package com.soyle.stories.theme.changeThemeDetails.changeCentralConflict

import com.soyle.stories.theme.usecases.changeThemeDetails.CentralConflictChanged

interface CentralConflictChangedReceiver {
    suspend fun receiveThemeWithCentralConflictChanged(centralConflictChanged: CentralConflictChanged)
}