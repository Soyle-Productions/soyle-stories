package com.soyle.stories.usecase.theme

import com.soyle.stories.domain.shouldBe
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.theme.makeOppositionValue
import com.soyle.stories.domain.theme.makeTheme
import com.soyle.stories.domain.theme.makeValueWeb
import com.soyle.stories.domain.theme.oppositionValue.OppositionValue
import com.soyle.stories.domain.theme.valueWeb.ValueWeb
import com.soyle.stories.domain.theme.valueWeb.ValueWebDoesNotContainOppositionValue
import com.soyle.stories.usecase.repositories.ThemeRepositoryDouble
import com.soyle.stories.usecase.theme.removeOppositionFromValueWeb.OppositionRemovedFromValueWeb
import com.soyle.stories.usecase.theme.removeOppositionFromValueWeb.RemoveOppositionFromValueWeb
import com.soyle.stories.usecase.theme.removeOppositionFromValueWeb.RemoveOppositionFromValueWebUseCase
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