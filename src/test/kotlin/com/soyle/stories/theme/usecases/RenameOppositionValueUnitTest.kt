package com.soyle.stories.theme.usecases

import com.soyle.stories.common.shouldBe
import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.OppositionValue
import com.soyle.stories.entities.theme.ValueWeb
import com.soyle.stories.theme.*
import com.soyle.stories.doubles.ThemeRepositoryDouble
import com.soyle.stories.theme.usecases.renameOppositionValue.RenameOppositionValue
import com.soyle.stories.theme.usecases.renameOppositionValue.RenameOppositionValueUseCase
import com.soyle.stories.theme.usecases.renameOppositionValue.RenamedOppositionValue
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

        val oppositionName = "Opposition Name ${UUID.randomUUID()}"
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
        fun `invalid name throws exception`() {
            result = assertThrows<OppositionValueNameCannotBeBlank> {
                renameOppositionValue()
            }
        }

        @Test
        fun `same name throws duplicate operation exception`() {
            result = assertThrows<OppositionValueAlreadyHasName> {
                renameOppositionValue(oppositionName)
            }
            result shouldBe oppositionValueAlreadyHasName(oppositionValueId.uuid, oppositionName)
        }

        @Nested
        inner class `Valid Name` {

            val validName = "Some New Name ${UUID.randomUUID().toString().takeLast(3)}"

            init {
                renameOppositionValue(validName)
            }

            @Test
            fun `check output has correct properties`() {
                result shouldBe renamedOppositionValue(
                    themeId.uuid,
                    valueWebId.uuid,
                    oppositionValueId.uuid,
                    validName
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

    private fun renameOppositionValue(name: String = "")
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