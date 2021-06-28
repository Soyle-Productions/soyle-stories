package com.soyle.stories.usecase.theme.outlineMoralArgument

import java.util.*

interface GetMoralArgumentFrame {

    suspend operator fun invoke(themeId: UUID, output: OutputPort)

    class ResponseModel(
        val themeId: UUID,
        val themeLine: String,
        val moralProblem: String,
        val thematicRevelation: String
    )

    interface OutputPort {
        suspend fun receiveMoralArgumentFrame(response: ResponseModel)
    }

}