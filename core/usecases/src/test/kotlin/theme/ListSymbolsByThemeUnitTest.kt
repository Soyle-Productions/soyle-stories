package com.soyle.stories.usecase.theme

import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.usecase.repositories.ThemeRepositoryDouble
import com.soyle.stories.usecase.theme.listSymbolsByTheme.ListSymbolsByTheme
import com.soyle.stories.usecase.theme.listSymbolsByTheme.ListSymbolsByThemeUseCase
import com.soyle.stories.usecase.theme.listSymbolsByTheme.SymbolsByTheme
import com.soyle.stories.usecase.theme.listSymbolsByTheme.theme
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.*

class ListSymbolsByThemeUnitTest {

    private val projectId = Project.Id()

    @Test
    fun `no themes in project`() {
        whenSymbolsAreListedByTheme()
        expectEmptyResult()
    }

    @Test
    fun `themes with no symbols`() {
        givenThemes("A", "B", "C", "D")
        whenSymbolsAreListedByTheme()
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

    private fun givenThemesAndSymbols(graph: String)
    {
        val lines = graph.split("\n")
        lines.forEach {
            val (identifier, symbolIdentifiers) = it.split(": ")

        }
    }

    private fun identifierFor(id: UUID): String = identifiers.getValue(id)
    private fun idFor(identifier: String): UUID = ids.getValue(identifier)

    private var result: Any? = null

    private fun whenSymbolsAreListedByTheme() {
        val useCase: ListSymbolsByTheme = ListSymbolsByThemeUseCase(themeRepository)
        val output = object : ListSymbolsByTheme.OutputPort {
            override suspend fun symbolsListedByTheme(response: SymbolsByTheme) {
                result = response
            }
        }
        runBlocking {
            useCase.invoke(projectId.uuid, output)
        }
    }

    private fun expectEmptyResult() {
        val actual = result as SymbolsByTheme
        assertTrue(actual.isEmpty())
    }

    private fun expectThemes(expectedOutput: String) {
        val actual = result as SymbolsByTheme
        val themeIdentifiers = expectedOutput.split(",").map(String::trim)
        assertEquals(
            themeIdentifiers.toSet(),
            actual.themes.map { identifierFor(it.theme.themeId) }.toSet()
        ) { "Incorrect themes in output." }
        actual.themes.forEach {
            assertEquals(
                themeRepository.themes[Theme.Id(it.theme.themeId)]!!.name,
                it.theme.themeName
            )
        }
    }

}