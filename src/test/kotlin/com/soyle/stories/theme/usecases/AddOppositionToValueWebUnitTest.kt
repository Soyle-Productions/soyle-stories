package com.soyle.stories.theme.usecases

import com.soyle.stories.common.shouldBe
import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.OppositionValue
import com.soyle.stories.entities.theme.ValueWeb
import com.soyle.stories.theme.ValueWebDoesNotExist
import com.soyle.stories.theme.doubles.ThemeRepositoryDouble
import com.soyle.stories.theme.makeTheme
import com.soyle.stories.theme.makeValueWeb
import com.soyle.stories.theme.usecases.addOppositionToValueWeb.AddOppositionToValueWeb
import com.soyle.stories.theme.usecases.addOppositionToValueWeb.AddOppositionToValueWebUseCase
import com.soyle.stories.theme.usecases.addOppositionToValueWeb.OppositionAddedToValueWeb
import com.soyle.stories.theme.valueWebDoesNotExist
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class AddOppositionToValueWebUnitTest {

    private val valueWebId = ValueWeb.Id()

    private var result: Any? = null

    @Test
    fun `value web does not exist`() {
        result = assertThrows<ValueWebDoesNotExist> {
            addOppositionToValueWeb()
        }
        result shouldBe valueWebDoesNotExist(valueWebId.uuid)
    }

    @Test
    fun `value web exists`() {
        val valueWebName = "Wonder bread"
        val theme = givenValueWebExists(valueWebName)
        addOppositionToValueWeb()
        val createdOpposition = createdOpposition()!!
        assertEquals("$valueWebName 1", createdOpposition.name)
        result shouldBe oppositionAddedToValueWeb(theme.id.uuid, valueWebId.uuid, createdOpposition.id.uuid,
            createdOpposition.name
        )
    }

    @Test
    fun `add another opposition`() {
        val valueWebName = "Wonder bread"
        val theme = givenValueWebExists(valueWebName, 5)
        addOppositionToValueWeb()
        val createdOpposition = createdOpposition()!!
        assertEquals("$valueWebName 6", createdOpposition.name)
        result shouldBe oppositionAddedToValueWeb(theme.id.uuid, valueWebId.uuid, createdOpposition.id.uuid,
            createdOpposition.name
        )
    }

    private var updatedTheme: Theme? = null
    private val themeRepository = ThemeRepositoryDouble(onUpdateTheme = { updatedTheme = it })

    private fun givenValueWebExists(valueWebName: String, existingOppositionCount: Int = 0): Theme {
        val theme = makeTheme(valueWebs = listOf(makeValueWeb(valueWebId, valueWebName, List(existingOppositionCount) { OppositionValue("") })))
        themeRepository.themes[theme.id] = theme
        return theme
    }

    private fun createdOpposition(): OppositionValue?
    {
        return updatedTheme?.let {
            it.valueWebs.find { it.id == valueWebId }?.oppositions?.last()
        }
    }

    private fun addOppositionToValueWeb()
    {
        val useCase: AddOppositionToValueWeb = AddOppositionToValueWebUseCase(themeRepository)
        val output = object: AddOppositionToValueWeb.OutputPort {
            override suspend fun addedOppositionToValueWeb(response: OppositionAddedToValueWeb) {
                result = response
            }
        }
        runBlocking {
            useCase.invoke(valueWebId.uuid, output)
        }
    }

    private fun oppositionAddedToValueWeb(
        expectedThemeId: UUID,
        expectedValueWebId: UUID,
        expectedOppositionId: UUID,
        expectedOppositionName: String
    ): (Any?) -> Unit = { actual ->
        actual as OppositionAddedToValueWeb
        assertEquals(expectedThemeId, actual.themeId)
        assertEquals(expectedValueWebId, actual.valueWebId)
        assertEquals(expectedOppositionId, actual.oppositionValueId)
        assertEquals(expectedOppositionName, actual.oppositionValueName)
    }

}