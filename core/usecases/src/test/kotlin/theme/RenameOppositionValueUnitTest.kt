package com.soyle.stories.usecase.theme

import com.soyle.stories.domain.nonBlankStr
import com.soyle.stories.domain.shouldBe
import com.soyle.stories.domain.theme.*
import com.soyle.stories.domain.theme.oppositionValue.OppositionValue
import com.soyle.stories.domain.theme.oppositionValue.RenamedOppositionValue
import com.soyle.stories.domain.theme.valueWeb.ValueWeb
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.repositories.ThemeRepositoryDouble
import com.soyle.stories.usecase.theme.renameOppositionValue.RenameOppositionValue
import com.soyle.stories.usecase.theme.renameOppositionValue.RenameOppositionValueUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class RenameOppositionValueUnitTest {

    private val oppositionValueId = OppositionValue.Id()

    private var result: Any? = null

    @Nested
    inner class `Opposition Value Doesn't Exist` {

        init {
            result = assertThrows<OppositionValueDoesNotExist> {
                renameOppositionValue()
            }
        }

        @Test
        fun `check exception has correct entity id`() {
            result shouldBe oppositionValueDoesNotExist(oppositionValueId.uuid)
        }

    }

    @Nested
    inner class `Opposition Value Exists` {

        val oppositionName = nonBlankStr("Opposition Name ${UUID.randomUUID()}")
        val themeId: Theme.Id
        val valueWebId: ValueWeb.Id

        init {
            val valueWeb = makeValueWeb(oppositions = listOf(makeOppositionValue(oppositionValueId, oppositionName)))
            val theme = makeTheme(valueWebs = listOf(valueWeb))
            themeRepository.themes[theme.id] = theme
            themeId = theme.id
            valueWebId = valueWeb.id
        }

        @Test
        fun `same name throws duplicate operation exception`() {
            result = assertThrows<OppositionValueAlreadyHasName> {
                renameOppositionValue(oppositionName)
            }
            result shouldBe oppositionValueAlreadyHasName(oppositionValueId.uuid, oppositionName.value)
        }

        @Nested
        inner class `Valid Name` {

            val validName = nonBlankStr("Some New Name ${UUID.randomUUID().toString().takeLast(3)}")

            init {
                renameOppositionValue(validName)
            }

            @Test
            fun `check output has correct properties`() {
                result shouldBe renamedOppositionValue(
                    themeId.uuid,
                    valueWebId.uuid,
                    oppositionValueId.uuid,
                    validName.value
                )
            }

            @Test
            fun `check theme updated`() {
                val updatedTheme = updatedTheme!!
                val updatedOppositionValue = updatedTheme.valueWebs.asSequence()
                    .flatMap { it.oppositions.asSequence() }
                    .find { it.id == oppositionValueId }!!
                assertEquals(validName, updatedOppositionValue.name) { "Opposition Value name is incorrect" }
            }

        }

    }

    private var updatedTheme: Theme? = null
    private val themeRepository = ThemeRepositoryDouble(onUpdateTheme = { updatedTheme = it })

    private fun renameOppositionValue(name: NonBlankString = nonBlankStr())
    {
        val useCase: RenameOppositionValue = RenameOppositionValueUseCase(themeRepository)
        val output = object : RenameOppositionValue.OutputPort {
            override suspend fun oppositionValueRenamed(response: RenamedOppositionValue) {
                result = response
            }
        }
        runBlocking {
            useCase.invoke(oppositionValueId.uuid, name, output)
        }
    }

    private fun renamedOppositionValue(
        expectedThemeId: UUID,
        expectedValueWebId: UUID,
        expectedOppositionValueId: UUID,
        expectedName: String
    ): (Any?) -> Unit = { actual ->
        actual as RenamedOppositionValue
        assertEquals(expectedThemeId, actual.themeId) { "[RenamedOppositionValue] has incorrect theme id" }
        assertEquals(expectedValueWebId, actual.valueWebId) { "[RenamedOppositionValue] has incorrect value web id" }
        assertEquals(expectedOppositionValueId, actual.oppositionValueId) { "[RenamedOppositionValue] has incorrect opposition value id" }
        assertEquals(expectedName, actual.oppositionValueName) { "[RenamedOppositionValue] has incorrect name" }
    }
}