package com.soyle.stories.theme.changeThemeDetails.changeCentralMoralQuestion

import com.soyle.stories.common.Notifier
import com.soyle.stories.usecase.theme.changeThemeDetails.ChangedCentralMoralQuestion

class ChangedCentralMoralQuestionNotifier : ChangedCentralMoralQuestionReceiver, Notifier<ChangedCentralMoralQuestionReceiver>() {

    override suspend fun receiveChangedCentralMoralQuestion(event: ChangedCentralMoralQuestion) {
        notifyAll { it.receiveChangedCentralMoralQuestion(event) }
    }

}