package com.soyle.stories.usecase.theme

import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.nonBlankStr
import com.soyle.stories.domain.shouldBe
import com.soyle.stories.domain.str
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.theme.makeTheme
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.repositories.ThemeRepositoryDouble
import com.soyle.stories.usecase.theme.changeThemeDetails.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ChangeThemeDetailsUnitTest {

    // preconditions
    private val theme = makeTheme()

    // post-conditions
    private var updatedTheme: Theme? = null

    // output
    private var result: Any? = null

    // setup
    private val themeRepository = ThemeRepositoryDouble(onUpdateTheme = ::updatedTheme::set)
    private val changeThemeDetails = ChangeThemeDetailsUseCase(themeRepository)

    @Nested
    inner class `Rename Theme` {

        private val useCase: RenameTheme = changeThemeDetails
        private val output = object : RenameTheme.OutputPort {
            override suspend fun themeRenamed(response: RenamedTheme) {
                result = response
            }
        }

        @Test
        fun `Theme does not exist`() {
            val error = degenerateTest<ThemeDoesNotExist> {
                renameTheme(nonBlankStr())
            }

            error shouldBe themeDoesNotExist(theme.id.uuid)
        }

        @Nested
        inner class `Input name is valid` {

            private val validName = nonBlankStr("Valid Theme Name ${str()}")

            init {
                themeRepository.givenTheme(theme)
            }

            @Test
            fun `should output renamed theme`() {
                renameTheme(validName)

                val result = result as RenamedTheme
                result.themeId.mustEqual(theme.id.uuid)
                result.originalName.mustEqual(theme.name)
                result.newName.mustEqual(validName.value)
            }

            @Test
            fun `should update theme in repository`() {
                renameTheme(validName)

                val updatedTheme = updatedTheme!!
                updatedTheme.mustEqual(theme.withName(validName.value))
            }

        }

        private fun renameTheme(inputName: NonBlankString) {
            runBlocking {
                useCase.invoke(theme.id.uuid, inputName, output)
            }
        }

    }

    @Nested
    inner class `Change Central Conflict` {

        private val useCase: ChangeCentralConflict = changeThemeDetails
        private val output = object : ChangeCentralConflict.OutputPort {
            override suspend fun centralConflictChanged(response: ChangeCentralConflict.ResponseModel) {
                result = response
            }
        }

        @Test
        fun `Theme does not exist`() {
            val error = degenerateTest<ThemeDoesNotExist> {
                changeCentralConflict("")
            }

            error shouldBe themeDoesNotExist(theme.id.uuid)
        }

        @Nested
        inner class `Theme exists` {

            private val newConflict = "New Conflict ${str()}"

            init {
                themeRepository.givenTheme(theme)
            }

            @Test
            fun `should update theme`() {
                changeCentralConflict(newConflict)

                val updatedTheme = updatedTheme!!
                updatedTheme.mustEqual(theme.withCentralConflict(newConflict))
            }

            @Test
            fun `should output central conflict changed in theme event`() {
                changeCentralConflict(newConflict)

                val result = result as ChangeCentralConflict.ResponseModel
                result.changedCentralConflict.themeId.mustEqual(theme.id.uuid)
                result.changedCentralConflict.centralConflict.mustEqual(newConflict)
            }

        }

        private fun changeCentralConflict(conflict: String) {
            runBlocking {
                useCase.invoke(theme.id.uuid, conflict, output)
            }
        }

    }

    @Nested
    inner class `Change Central Moral Question` {

        private val useCase: ChangeCentralMoralQuestion = changeThemeDetails
        private val output = object : ChangeCentralMoralQuestion.OutputPort {
            override suspend fun centralMoralQuestionChanged(response: ChangeCentralMoralQuestion.ResponseModel) {
                result = response
            }
        }

        @Test
        fun `Theme does not exist`() {
            val error = degenerateTest<ThemeDoesNotExist> {
                changeCentralMoralQuestion("")
            }

            error shouldBe themeDoesNotExist(theme.id.uuid)
        }

        @Nested
        inner class `Theme exists but same question has been input` {

            val inputQuestion = "The same central question ${str()}"

            init {
                themeRepository.givenTheme(theme.withMoralProblem(inputQuestion))
            }

            @Test
            fun `should output changed central moral question`() {
                changeCentralMoralQuestion(inputQuestion)

                val result = result as ChangeCentralMoralQuestion.ResponseModel
                result.changedCentralMoralQuestion.themeId.mustEqual(theme.id.uuid)
                result.changedCentralMoralQuestion.newQuestion.mustEqual(inputQuestion)
            }

            @Test
            fun `should not update theme`() {
                changeCentralMoralQuestion(inputQuestion)

                assertNull(updatedTheme)
            }

        }

        @Nested
        inner class `Theme exists and new question` {

            val inputQuestion = "New central question ${str()}"

            init {
                themeRepository.givenTheme(theme)
            }

            @Test
            fun `should output changed central moral question`() {
                changeCentralMoralQuestion(inputQuestion)

                val result = result as ChangeCentralMoralQuestion.ResponseModel
                result.changedCentralMoralQuestion.themeId.mustEqual(theme.id.uuid)
                result.changedCentralMoralQuestion.newQuestion.mustEqual(inputQuestion)
            }

            @Test
            fun `should update theme`() {
                changeCentralMoralQuestion(inputQuestion)

                updatedTheme.mustEqual(theme.withMoralProblem(inputQuestion))
            }

        }

        private fun changeCentralMoralQuestion(question: String) {
            runBlocking {
                useCase.invoke(theme.id.uuid, question, output)
            }
        }

    }

    @Nested
    inner class `Change Theme Line` {

        private val useCase: ChangeThemeLine = changeThemeDetails
        private val output = object : ChangeThemeLine.OutputPort {
            override suspend fun themeLineChanged(response: ChangeThemeLine.ResponseModel) {
                result = response
            }
        }

        @Test
        fun `Theme does not exist`() {
            val error = degenerateTest<ThemeDoesNotExist> {
                changeThemeLine("")
            }

            error shouldBe themeDoesNotExist(theme.id.uuid)
        }

        @Nested
        inner class `Theme exists` {

            val inputThemeLine = "New theme line ${str()}"

            init {
                themeRepository.givenTheme(theme)
            }

            @Test
            fun `should output changed theme line`() {
                changeThemeLine(inputThemeLine)

                val result = result as ChangeThemeLine.ResponseModel
                result.changedThemeLine.themeId.mustEqual(theme.id.uuid)
                result.changedThemeLine.newThemeLine.mustEqual(inputThemeLine)
            }

            @Test
            fun `should update theme`() {
                changeThemeLine(inputThemeLine)

                updatedTheme.mustEqual(theme.withThemeLine(inputThemeLine))
            }

        }

        private fun changeThemeLine(themeLine: String) {
            runBlocking {
                useCase.invoke(theme.id.uuid, themeLine, output)
            }
        }

    }

    @Nested
    inner class `Change Thematic Revelation` {

        private val useCase: ChangeThematicRevelation = changeThemeDetails
        private val output = object : ChangeThematicRevelation.OutputPort {
            override suspend fun thematicRevelationChanged(response: ChangeThematicRevelation.ResponseModel) {
                result = response
            }
        }

        @Test
        fun `Theme does not exist`() {
            val error = degenerateTest<ThemeDoesNotExist> {
                changeThematicRevelation("")
            }

            error shouldBe themeDoesNotExist(theme.id.uuid)
        }

        @Nested
        inner class `Theme exists` {

            val inputRevelation = "New thematic revelation ${str()}"

            init {
                themeRepository.givenTheme(theme)
            }

            @Test
            fun `should output changed thematic revelation`() {
                changeThematicRevelation(inputRevelation)

                val result = result as ChangeThematicRevelation.ResponseModel
                result.changedThematicRevelation.themeId.mustEqual(theme.id.uuid)
                result.changedThematicRevelation.newRevelation.mustEqual(inputRevelation)
            }

            @Test
            fun `should update theme`() {
                changeThematicRevelation(inputRevelation)

                updatedTheme.mustEqual(theme.withThematicRevelation(inputRevelation))
            }

        }

        private fun changeThematicRevelation(revelation: String) {
            runBlocking {
                useCase.invoke(theme.id.uuid, revelation, output)
            }
        }

    }

    private inline fun <reified T : Throwable> degenerateTest(noinline method: () -> Unit): T {
        val thrown = assertThrows<T>(method)
        assertNull(updatedTheme)
        return thrown
    }

}