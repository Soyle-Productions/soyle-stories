package com.soyle.stories.characterarc.eventbus

import com.soyle.stories.eventbus.Notifier
import com.soyle.stories.theme.ThemeException
import com.soyle.stories.theme.usecases.changeCentralMoralQuestion.ChangeCentralMoralQuestion

class ChangeCentralMoralQuestionNotifier : ChangeCentralMoralQuestion.OutputPort, Notifier<ChangeCentralMoralQuestion.OutputPort>() {

    override fun receiveChangeCentralMoralQuestionResponse(response: ChangeCentralMoralQuestion.ResponseModel) {
        notifyAll { it.receiveChangeCentralMoralQuestionResponse(response) }
    }

    override fun receiveChangeCentralMoralQuestionFailure(failure: ThemeException) {
        notifyAll { it.receiveChangeCentralMoralQuestionFailure(failure) }
    }

}