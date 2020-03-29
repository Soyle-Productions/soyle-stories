package com.soyle.stories.theme.usecases.changeCentralMoralQuestion

import arrow.core.identity
import com.soyle.stories.entities.Theme
import com.soyle.stories.theme.Context
import com.soyle.stories.theme.ThemeDoesNotExist
import com.soyle.stories.theme.ThemeException
import com.soyle.stories.theme.usecases.changeCentralMoralQuestion.ChangeCentralMoralQuestion.OutputPort
import com.soyle.stories.theme.usecases.changeCentralMoralQuestion.ChangeCentralMoralQuestion.ResponseModel
import java.util.*

class ChangeCentralMoralQuestionUseCase(
    private val context: Context
) : ChangeCentralMoralQuestion {

    override suspend fun invoke(themeId: UUID, question: String, output: OutputPort) {
        val response = try {
            changeCentralMoralQuestionOfTheme(Theme.Id(themeId), question) ?: return
        } catch (t: ThemeException) {
            return output.receiveChangeCentralMoralQuestionFailure(t)
        }
        output.receiveChangeCentralMoralQuestionResponse(response)
    }

    private suspend fun changeCentralMoralQuestionOfTheme(themeId: Theme.Id, question: String): ResponseModel? {
        val theme = getTheme(themeId)
        if (theme.centralMoralQuestion != question) {
            val newTheme = theme.withNewQuestion(question)
            context.themeRepository.updateTheme(newTheme)
            return respondWith(newTheme)
        }
        return null
    }

    private fun Theme.withNewQuestion(question: String): Theme {
        return changeCentralMoralQuestion(question).fold(
            { throw it },
            ::identity
        )
    }

    private fun respondWith(theme: Theme): ResponseModel {
        return ResponseModel(
            theme.id.uuid,
            theme.centralMoralQuestion
        )
    }

    private suspend fun getTheme(themeId: Theme.Id): Theme {
        return context.themeRepository.getThemeById(themeId)
            ?: throw ThemeDoesNotExist(themeId.uuid)
    }

}