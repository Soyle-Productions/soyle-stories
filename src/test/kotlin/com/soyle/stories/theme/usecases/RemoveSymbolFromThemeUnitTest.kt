package com.soyle.stories.theme.usecases

import com.soyle.stories.common.shouldBe
import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.Symbol
import com.soyle.stories.theme.SymbolDoesNotExist
import com.soyle.stories.theme.doubles.ThemeRepositoryDouble
import com.soyle.stories.theme.makeTheme
import com.soyle.stories.theme.repositories.ThemeRepository
import com.soyle.stories.theme.symbolDoesNotExist
import com.soyle.stories.theme.usecases.removeSymbolFromTheme.RemoveSymbolFromTheme
import com.soyle.stories.theme.usecases.removeSymbolFromTheme.RemoveSymbolFromThemeUseCase
import com.soyle.stories.theme.usecases.removeSymbolFromTheme.SymbolRemovedFromTheme
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class RemoveSymbolFromThemeUnitTest {

    private val symbolId = Symbol.Id()
    private val symbolName = "Symbol Name ${UUID.randomUUID()}"

    private var result: Any? = null
    private val output = object : RemoveSymbolFromTheme.OutputPort {
        override suspend fun removedSymbolFromTheme(response: SymbolRemovedFromTheme) {
            result = response
        }
    }

    private var updatedTheme: Theme? = null

    @Test
    fun `symbol does not exist`() {
        result = assertThrows<SymbolDoesNotExist> {
            removeSymbolFromTheme()
        }
        assertNull(updatedTheme)
        result shouldBe symbolDoesNotExist(symbolId.uuid)
    }

    @Test
    fun `symbol exists`() {
        val theme = givenSymbolExists()
        removeSymbolFromTheme()
        assertEquals(theme.withoutSymbol(symbolId), updatedTheme!!)
        result shouldBe ::symbolRemovedFromTheme
    }

    private val repository = ThemeRepositoryDouble(onUpdateTheme = {
        updatedTheme = it
    })

    private fun givenSymbolExists(): Theme {
        val theme = makeTheme(
            symbols = listOf(
                Symbol(symbolId, symbolName)
            )
        )
        repository.apply {
            themes[theme.id] = theme
        }
        return theme
    }

    private fun removeSymbolFromTheme()
    {
        runBlocking {
            RemoveSymbolFromThemeUseCase(repository).invoke(symbolId.uuid, output)
        }
    }

    private fun symbolRemovedFromTheme(actual: Any?)
    {
        actual as SymbolRemovedFromTheme
        assertEquals(updatedTheme!!.id.uuid, actual.themeId)
        assertEquals(symbolId.uuid, actual.symbolId)
        assertEquals(symbolName, actual.symbolName)
    }

}