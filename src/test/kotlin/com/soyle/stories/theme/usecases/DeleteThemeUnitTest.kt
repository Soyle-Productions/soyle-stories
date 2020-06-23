package com.soyle.stories.theme.usecases

import com.soyle.stories.common.shouldBe
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Theme
import com.soyle.stories.theme.doubles.ThemeRepositoryDouble
import com.soyle.stories.theme.makeTheme
import com.soyle.stories.theme.themeDoesNotExist
import com.soyle.stories.theme.usecases.deleteTheme.DeleteTheme
import com.soyle.stories.theme.usecases.deleteTheme.DeleteThemeUseCase
import com.soyle.stories.theme.usecases.deleteTheme.DeletedTheme
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class DeleteThemeUnitTest {

    private val themeId = Theme.Id()

    private var deletedTheme: Theme? = null
    private var result: Any? = null

    @Test
    fun `theme does not exist`() {
        whenThemeIsDeleted()
        result shouldBe themeDoesNotExist(themeId.uuid)
    }

    @Test
    fun `theme exists`() {
        givenThemeExists()
        whenThemeIsDeleted()
        assertNotNull(deletedTheme)
        result shouldBe ::deletedTheme
    }

    private val themeRepository = ThemeRepositoryDouble(onDeleteTheme = { deletedTheme = it })

    private fun givenThemeExists()
    {
        themeRepository.themes[themeId] = makeTheme(themeId)
    }

    private fun whenThemeIsDeleted()
    {
        val useCase: DeleteTheme = DeleteThemeUseCase(themeRepository)
        val output = object : DeleteTheme.OutputPort {
            override fun themeDeleted(response: DeletedTheme) {
                result = response
            }
        }
        runBlocking {
            try { useCase.invoke(themeId.uuid, output) }
            catch (t: Throwable) { result = t }
        }
    }

    private fun deletedTheme(actual: Any?)
    {
        actual as DeletedTheme
        assertEquals(themeId.uuid, actual.themeId)
    }

}