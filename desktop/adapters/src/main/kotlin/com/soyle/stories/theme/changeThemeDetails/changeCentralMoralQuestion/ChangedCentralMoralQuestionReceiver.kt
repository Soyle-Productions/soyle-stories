package com.soyle.stories.theme.changeThemeDetails.changeCentralMoralQuestion

import com.soyle.stories.usecase.theme.changeThemeDetails.ChangedCentralMoralQuestion

interface ChangedCentralMoralQuestionReceiver {

    suspend fun receiveChangedCentralMoralQuestion(event: ChangedCentralMoralQuestion)

}