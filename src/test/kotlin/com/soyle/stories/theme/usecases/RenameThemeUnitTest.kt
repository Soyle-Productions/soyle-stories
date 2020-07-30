package com.soyle.stories.theme.usecases

import com.soyle.stories.common.shouldBe
import com.soyle.stories.entities.Theme
import com.soyle.stories.doubles.ThemeRepositoryDouble
import com.soyle.stories.theme.makeTheme
import com.soyle.stories.theme.themeDoesNotExist
import com.soyle.stories.theme.themeNameCannotBeBlank
import com.soyle.stories.theme.usecases.updateThemeMetaData.RenameTheme
import com.soyle.stories.theme.usecases.updateThemeMetaData.RenameThemeUseCase
import com.soyle.stories.theme.usecases.updateThemeMetaData.RenamedTheme
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*

class RenameThemeUnitTest {

    private val themeId = Theme.Id()
    private val oldName = "Old Theme Name ${UUID.randomUUID()}"

    private var updatedTheme: Theme? = null
    private var result: Any? = null

    @Test
    fun `theme does not exist`() {
        whenThemeIsRenamed()
        assertNull(updatedTheme)
        result shouldBe themeDoesNotExist(themeId.uuid)
    }

    @Test
    fun `invalid name`() {
        givenThemeExists()
        whenThemeIsRenamed()
        assertNull(updatedTheme)
        result shouldBe ::themeNameCannotBeBlank
    }

    @Test
    fun `valid name`() {
        val name = "Valid Theme Name ${UUID.randomUUID()}"
        givenThemeExists()
        whenThemeIsRenamed(name)
        assertThemeRenamed(name)
        result shouldBe renamedTheme(name)
    }

    private val themeRepository = ThemeRepositoryDouble(onUpdateTheme = { updatedTheme = it })

    private fun givenThemeExists()
    {
        themeRepository.themes[themeId] = makeTheme(themeId, name = oldName)
    }

    private fun whenThemeIsRenamed(name: String = "")
    {
        val useCase: RenameTheme = RenameThemeUseCase(themeRepository)
        val output = object : RenameTheme.OutputPort {
            override fun themeRenamed(response: RenamedTheme) {
                result = response
            }
        }
        runBlocking {
            try { useCase.invoke(themeId.uuid, name, output) }
            catch (t: Throwable) { result = t }
        }
    }

    private fun assertThemeRenamed(name: String)
    {
        assertEquals(themeId, updatedTheme!!.id)
        assertEquals(name, updatedTheme!!.name)
    }

    private fun renamedTheme(newName: String): (Any?) -> Unit = { actual ->
        actual as RenamedTheme
        assertEquals(themeId.uuid, actual.themeId)
        assertEquals(oldName, actual.originalName)
        assertEquals(newName, actual.newName)
    }

}