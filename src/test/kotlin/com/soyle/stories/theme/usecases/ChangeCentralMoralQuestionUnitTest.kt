package com.soyle.stories.theme.usecases

import com.soyle.stories.entities.Theme
import com.soyle.stories.theme.*
import com.soyle.stories.theme.usecases.changeCentralMoralQuestion.ChangeCentralMoralQuestion
import com.soyle.stories.theme.usecases.changeCentralMoralQuestion.ChangeCentralMoralQuestionUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class ChangeCentralMoralQuestionUnitTest {

    private val themeId = UUID.randomUUID()
    private val inputQuestion = "What is the meaning of life?"

    private lateinit var context: Context
    private var updatedTheme: Theme? = null
    private var result: Any? = null

    @BeforeEach
    fun clear() {
        result = null
        context = setupContext()
        updatedTheme = null
    }

    @Test
    fun `theme doesn't exist`() {
        givenNoThemes()
        whenUseCaseIsExecuted()
        (result as ThemeDoesNotExist).themeIdMustEqual(themeId)
    }

    @Test
    fun `theme exists`() {
        givenThemeWithId(themeId)
        whenUseCaseIsExecuted()
        assertEquals(themeId,
            (result as ChangeCentralMoralQuestion.ResponseModel).themeId
        )
    }

    @Test
    fun `persist new question`() {
        givenThemeWithId(themeId)
        whenUseCaseIsExecuted()
        assertEquals(inputQuestion,
            (updatedTheme as Theme).centralMoralQuestion
        )
    }

    @Test
    fun `output new question`() {
        givenThemeWithId(themeId)
        whenUseCaseIsExecuted()
        assertEquals(inputQuestion,
            (result as ChangeCentralMoralQuestion.ResponseModel).newQuestion
        )
    }

    @Test
    fun `don't persist non-change`() {
        givenThemeWithId(themeId, andQuestion=inputQuestion)
        whenUseCaseIsExecuted()
        assertNull(updatedTheme)
    }

    @Test
    fun `don't output non-change`() {
        givenThemeWithId(themeId, andQuestion=inputQuestion)
        whenUseCaseIsExecuted()
        assertNull(result)
    }

    private fun givenNoThemes() {
        context = setupContext(
            initialThemes = emptyList(),
            updateTheme = {
                updatedTheme = it
            }
        )
    }
    private fun givenThemeWithId(themeId: UUID, andQuestion: String = "") {
        context = setupContext(
            initialThemes = listOf(Theme(Theme.Id(themeId), andQuestion, emptyMap(), emptyMap())),
            updateTheme = {
                updatedTheme = it
            }
        )
    }

    private fun whenUseCaseIsExecuted() {
        val output = object : ChangeCentralMoralQuestion.OutputPort {
            var result: Any? = null
            override fun receiveChangeCentralMoralQuestionFailure(failure: ThemeException) {
                result = failure
            }

            override fun receiveChangeCentralMoralQuestionResponse(response: ChangeCentralMoralQuestion.ResponseModel) {
                result = response
            }
        }
        val useCase: ChangeCentralMoralQuestion = ChangeCentralMoralQuestionUseCase(context)
        runBlocking {
            useCase.invoke(themeId, inputQuestion, output)
        }
        result = output.result
    }
}