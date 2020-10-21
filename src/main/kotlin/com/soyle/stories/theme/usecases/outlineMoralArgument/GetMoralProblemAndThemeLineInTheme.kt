package com.soyle.stories.theme.usecases.outlineMoralArgument

import java.util.*

interface GetMoralProblemAndThemeLineInTheme {

    suspend operator fun invoke(themeId: UUID, output: OutputPort)

    class ResponseModel(
        val themeId: UUID,
        val themeLine: String,
        val moralProblem: String
    )

    interface OutputPort {
        suspend fun receiveMoralProblemAndThemeLineInTheme(response: ResponseModel)
    }

}