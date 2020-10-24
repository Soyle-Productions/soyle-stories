package com.soyle.stories.theme.changeThemeDetails

import com.soyle.stories.theme.usecases.changeThemeDetails.ChangedCentralMoralQuestion

interface ChangedCentralMoralQuestionReceiver {

    suspend fun receiveChangedCentralMoralQuestion(event: ChangedCentralMoralQuestion)

}