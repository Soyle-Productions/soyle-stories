package com.soyle.stories.theme.usecases.changeCentralMoralQuestion

import com.soyle.stories.theme.ThemeException
import java.util.*

interface ChangeCentralMoralQuestion {

    suspend operator fun invoke(themeId: UUID, question: String, output: OutputPort)

    class ResponseModel(val themeId: UUID, val newQuestion: String)

    interface OutputPort {
        fun receiveChangeCentralMoralQuestionFailure(failure: ThemeException)
        fun receiveChangeCentralMoralQuestionResponse(response: ResponseModel)
    }

}