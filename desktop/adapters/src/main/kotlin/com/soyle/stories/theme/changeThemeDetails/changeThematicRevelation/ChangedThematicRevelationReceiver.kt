package com.soyle.stories.theme.changeThemeDetails.changeThematicRevelation

import com.soyle.stories.usecase.theme.changeThemeDetails.ChangedThematicRevelation

interface ChangedThematicRevelationReceiver {

    suspend fun receiveChangedThematicRevelation(event: ChangedThematicRevelation)

}