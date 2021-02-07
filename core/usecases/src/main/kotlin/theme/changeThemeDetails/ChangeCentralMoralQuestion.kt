package com.soyle.stories.usecase.theme.changeThemeDetails

import java.util.*

interface ChangeCentralMoralQuestion {

    suspend operator fun invoke(themeId: UUID, question: String, output: OutputPort)

    class ResponseModel(
        val changedCentralMoralQuestion: ChangedCentralMoralQuestion
    )

    interface OutputPort {
        suspend fun centralMoralQuestionChanged(response: ResponseModel)
    }

}