package com.soyle.stories.theme.changeThemeDetails.changeThemeLine

import com.soyle.stories.usecase.theme.changeThemeDetails.ChangedThemeLine

interface ChangedThemeLineReceiver {

    suspend fun receiveChangedThemeLine(event: ChangedThemeLine)

}