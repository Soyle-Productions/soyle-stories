package com.soyle.stories.theme.usecases

import com.soyle.stories.common.shouldBe
import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.OppositionValue
import com.soyle.stories.entities.theme.ValueWeb
import com.soyle.stories.theme.*
import com.soyle.stories.doubles.ThemeRepositoryDouble
import com.soyle.stories.theme.usecases.removeOppositionFromValueWeb.OppositionRemovedFromValueWeb
import com.soyle.stories.theme.usecases.removeOppositionFromValueWeb.RemoveOppositionFromValueWeb
import com.soyle.stories.theme.usecases.removeOppositionFromValueWeb.RemoveOppositionFromValueWebUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class RemoveOppositionFromValueWebUnitTest {

    private val themeId = Theme.Id()
    private val valueWebId = ValueWeb.Id()
    private val oppositionId = OppositionValue.Id()

    private var result: Any? = null
    private var updatedTheme: Theme? = null

    @Test
    fun `value web does not exist`() {
        result = assertThrows<ValueWebDoesNotExist> {
            removeOppositionFromValueWeb()
        }
        result shouldBe valueWebDoesNotExist(valueWebId.uuid)
    }

    @Test
    fun `value web does not contain opposition`() {
        givenValueWeb()
        result = assertThrows<ValueWebDoesNotContainOppositionValue> {
            removeOppositionFromValueWeb()
        }
        result shouldBe valueWebDoesNotContainOppositionValue(valueWebId.uuid, oppositionId.uuid)
    }

    @Test
    fun `value web contains opposition`() {
        givenValueWeb(hasOpposition = true)
        removeOppositionFromValueWeb()
        updatedTheme shouldBe ::themeWithoutOppositionValue
        result shouldBe ::oppositionRemovedFromValueWeb
    }

    private val themeRepository = ThemeRepositoryDouble(onUpdateTheme = { updatedTheme = it })

    private fun givenValueWeb(hasOpposition: Boolean = false)
    {
        themeRepository.themes[themeId] = makeTheme(themeId, valueWebs = listOf(
            makeValueWeb(valueWebId, oppositions = listOfNotNull(
                if (hasOpposition) makeOppositionValue(oppositionId) else null
            ))
        ))
    }

    private fun removeOppositionFromValueWeb()
    {
        val useCase: RemoveOppositionFromValueWeb = RemoveOppositionFromValueWebUseCase(themeRepository)
        val output = object : RemoveOppositionFromValueWeb.OutputPort {
            override suspend fun removedOppositionFromValueWeb(response: OppositionRemovedFromValueWeb) {
                result = response
            }
        }
        runBlocking {
            useCase.invoke(oppositionId.uuid, valueWebId.uuid, output)
        }
    }

    private fun themeWithoutOppositionValue(actual: Any?) {
        actual as Theme
        assertEquals(themeId, actual.id)
        val valueWeb = actual.valueWebs.find { it.id == valueWebId }!!
        assertNull(valueWeb.oppositions.find { it.id == oppositionId })
    }

    private fun oppositionRemovedFromValueWeb(actual: Any?) {
        actual as OppositionRemovedFromValueWeb
        assertEquals(themeId.uuid, actual.themeId)
        assertEquals(valueWebId.uuid, actual.valueWebId)
        assertEquals(oppositionId.uuid, actual.oppositionValueId)
    }

}