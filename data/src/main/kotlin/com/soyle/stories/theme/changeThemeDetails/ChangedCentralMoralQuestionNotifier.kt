package com.soyle.stories.theme.changeThemeDetails

import com.soyle.stories.common.Notifier
import com.soyle.stories.theme.usecases.changeThemeDetails.ChangedCentralMoralQuestion

class ChangedCentralMoralQuestionNotifier : ChangedCentralMoralQuestionReceiver, Notifier<ChangedCentralMoralQuestionReceiver>() {

    override suspend fun receiveChangedCentralMoralQuestion(event: ChangedCentralMoralQuestion) {
        notifyAll { it.receiveChangedCentralMoralQuestion(event) }
    }

}