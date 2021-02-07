package com.soyle.stories.usecase.theme

import com.soyle.stories.domain.shouldBe
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.theme.makeTheme
import com.soyle.stories.domain.theme.makeValueWeb
import com.soyle.stories.domain.theme.valueWeb.ValueWeb
import com.soyle.stories.usecase.repositories.ThemeRepositoryDouble
import com.soyle.stories.usecase.theme.removeValueWebFromTheme.RemoveValueWebFromTheme
import com.soyle.stories.usecase.theme.removeValueWebFromTheme.RemoveValueWebFromThemeUseCase
import com.soyle.stories.usecase.theme.removeValueWebFromTheme.ValueWebRemovedFromTheme
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class RemoveValueWebFromThemeUnitTest {

    private val themeId = Theme.Id()
    private val valueWebId = ValueWeb.Id()

    private var updatedTheme: Theme? = null
    private var result: Any? = null

    @Test
    fun `value web does not exist`() {
        result = assertThrows<ValueWebDoesNotExist> {
            removeValueWebFromTheme()
        }
        assertNull(updatedTheme)
        result shouldBe valueWebDoesNotExist(valueWebId.uuid)
    }

    @Test
    fun `value web exists`() {
        givenValueWeb()
        removeValueWebFromTheme()
        updatedTheme shouldBe ::themeWithoutValueWeb
        result shouldBe ::valueWebRemovedFromTheme
    }

    private val themeRepository = ThemeRepositoryDouble(onUpdateTheme = { updatedTheme = it })

    private fun givenValueWeb()
    {
        themeRepository.themes[themeId] = makeTheme(themeId, valueWebs = listOf(makeValueWeb(valueWebId)))
    }

    private fun removeValueWebFromTheme()
    {
        val useCase: RemoveValueWebFromTheme = RemoveValueWebFromThemeUseCase(themeRepository)
        val output = object : RemoveValueWebFromTheme.OutputPort {
            override suspend fun removedValueWebFromTheme(response: ValueWebRemovedFromTheme) {
                result = response
            }
        }
        runBlocking {
            useCase.invoke(valueWebId.uuid, output)
        }
    }

    private fun themeWithoutValueWeb(actual: Any?)
    {
        actual as Theme
        assertEquals(themeId, actual.id)
        assertNull(actual.valueWebs.find { it.id == valueWebId })
    }

    private fun valueWebRemovedFromTheme(actual: Any?)
    {
        actual as ValueWebRemovedFromTheme
        assertEquals(themeId.uuid, actual.themeId)
        assertEquals(valueWebId.uuid, actual.valueWebId)
    }

}