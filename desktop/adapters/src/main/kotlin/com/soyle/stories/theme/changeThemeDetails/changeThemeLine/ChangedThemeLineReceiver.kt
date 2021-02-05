package com.soyle.stories.theme.changeThemeDetails.changeThemeLine

import com.soyle.stories.theme.usecases.changeThemeDetails.ChangedThemeLine

interface ChangedThemeLineReceiver {

    suspend fun receiveChangedThemeLine(event: ChangedThemeLine)

}