package com.soyle.stories.theme.usecases

import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Theme
import com.soyle.stories.theme.doubles.ThemeRepositoryDouble
import com.soyle.stories.theme.usecases.listSymbolsByTheme.ListSymbolsByTheme
import com.soyle.stories.theme.usecases.listSymbolsByTheme.ListSymbolsByThemeUseCase
import com.soyle.stories.theme.usecases.listSymbolsByTheme.SymbolsByTheme
import com.soyle.stories.theme.usecases.listSymbolsByTheme.theme
import com.soyle.stories.theme.usecases.listThemes.ListThemes
import com.soyle.stories.theme.usecases.listThemes.ListThemesUseCase
import com.soyle.stories.theme.usecases.listThemes.ThemeList
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.*

class ListThemesUnitTest {

    private val projectId = Project.Id()

    @Test
    fun `no themes in project`() {
        whenThemesAreListed()
        expectEmptyResult()
    }

    @Test
    fun `some themes in project`() {
        givenThemes("A", "B", "C", "D")
        whenThemesAreListed()
        expectThemes("A, B, C, D")
    }

    private val themeRepository = ThemeRepositoryDouble()
    private val identifiers = mutableMapOf<UUID, String>()
    private val ids = mutableMapOf<String, UUID>()

    private fun givenThemes(vararg themes: String) {
        themeRepository.themes.putAll(themes.map { identifier ->
            Theme(projectId, identifier).also {
                identifiers[it.id.uuid] = identifier
                ids[identifier] = it.id.uuid
            }
        }.associateBy { it.id })
    }

    private fun identifierFor(id: UUID): String = identifiers.getValue(id)
    private fun idFor(identifier: String): UUID = ids.getValue(identifier)

    private var result: Any? = null

    private fun whenThemesAreListed() {
        val useCase: ListThemes = ListThemesUseCase(themeRepository)
        val output = object : ListThemes.OutputPort {
            override suspend fun themesListed(response: ThemeList) {
                result = response
            }
        }
        runBlocking {
            useCase.invoke(projectId.uuid, output)
        }
    }

    private fun expectEmptyResult() {
        val actual = result as ThemeList
        assertTrue(actual.isEmpty())
    }

    private fun expectThemes(expectedOutput: String) {
        val actual = result as ThemeList
        val themeIdentifiers = expectedOutput.split(",").map(String::trim)
        assertEquals(
            themeIdentifiers.toSet(),
            actual.themes.map { identifierFor(it.themeId) }.toSet()
        ) { "Incorrect themes in output." }
        actual.themes.forEach {
            assertEquals(
                themeRepository.themes[Theme.Id(it.themeId)]!!.name,
                it.themeName
            )
        }
    }

}