package com.soyle.stories.characterarc.changeCentralMoralQuestion

import com.soyle.stories.common.Notifier
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.theme.ThemeException
import com.soyle.stories.theme.usecases.changeCentralMoralQuestion.ChangeCentralMoralQuestion

class ChangeCentralMoralQuestionNotifier(
    private val threadTransformer: ThreadTransformer
) : ChangeCentralMoralQuestion.OutputPort, Notifier<ChangeCentralMoralQuestion.OutputPort>() {

    override fun receiveChangeCentralMoralQuestionResponse(response: ChangeCentralMoralQuestion.ResponseModel) {
        threadTransformer.async {
            notifyAll { it.receiveChangeCentralMoralQuestionResponse(response) }
        }
    }

    override fun receiveChangeCentralMoralQuestionFailure(failure: ThemeException) {
        threadTransformer.async {
            notifyAll { it.receiveChangeCentralMoralQuestionFailure(failure) }
        }
    }

}