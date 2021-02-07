package com.soyle.stories.usecase.theme

import com.soyle.stories.domain.shouldBe
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.theme.Symbol
import com.soyle.stories.usecase.repositories.ThemeRepositoryDouble
import com.soyle.stories.domain.theme.makeTheme
import com.soyle.stories.usecase.theme.listSymbolsInTheme.ListSymbolsInTheme
import com.soyle.stories.usecase.theme.listSymbolsInTheme.ListSymbolsInThemeUseCase
import com.soyle.stories.usecase.theme.listSymbolsInTheme.SymbolsInTheme
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class ListSymbolsInThemeUnitTest {

    private val themeId = Theme.Id()
    private val themeName = "Theme Name ${UUID.randomUUID().toString().takeLast(3)}"

    private var result: Any? = null

    @Test
    fun `theme does not exist`() {
        result = assertThrows<ThemeDoesNotExist> {
            listSymbolsInTheme()
        }
        result shouldBe themeDoesNotExist(themeId.uuid)
    }

    @Test
    fun `theme has no symbols`() {
        givenTheme()
        listSymbolsInTheme()
        result shouldBe symbolsInTheme()
    }

    @Test
    fun `theme has symbols`() {
        val symbolCount = 5
        givenTheme(symbolCount)
        listSymbolsInTheme()
        result shouldBe symbolsInTheme(symbolCount)
    }

    private val themeRepository = ThemeRepositoryDouble()

    private fun givenTheme(symbolCount: Int = 0) {
        themeRepository.themes[themeId] = makeTheme(themeId, name = themeName, symbols = List(symbolCount) {
            Symbol("Symbol $it")
        })
    }

    private fun listSymbolsInTheme() {
        val useCase: ListSymbolsInTheme = ListSymbolsInThemeUseCase(themeRepository)
        val output = object : ListSymbolsInTheme.OutputPort {
            override suspend fun symbolsListedInTheme(response: SymbolsInTheme) {
                result = response
            }
        }
        runBlocking {
            useCase.invoke(themeId.uuid, output)
        }
    }

    private fun symbolsInTheme(expectedSymbolCount: Int = 0): (Any?) -> Unit = { actual ->
        actual as SymbolsInTheme
        assertEquals(themeId.uuid, actual.themeId)
        assertEquals(themeName, actual.themeName)
        assertTrue(actual.symbols.all { it is SymbolItem })
        assertEquals(expectedSymbolCount, actual.symbols.size)
        val symbols = themeRepository.themes[themeId]!!.symbols.associateBy { it.id.uuid }
        actual.symbols.forEach {
            val backingSymbol = symbols.getValue(it.symbolId)
            assertEquals(backingSymbol.name, it.symbolName)
        }
    }

}