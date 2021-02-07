package com.soyle.stories.usecase.theme

import com.soyle.stories.domain.nonBlankStr
import com.soyle.stories.domain.shouldBe
import com.soyle.stories.domain.theme.Symbol
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.theme.makeTheme
import com.soyle.stories.domain.theme.symbolName
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.repositories.ThemeRepositoryDouble
import com.soyle.stories.usecase.theme.addSymbolToTheme.AddSymbolToTheme
import com.soyle.stories.usecase.theme.addSymbolToTheme.AddSymbolToThemeUseCase
import com.soyle.stories.usecase.theme.addSymbolToTheme.SymbolAddedToTheme
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class AddSymbolToThemeUnitTest {

    private val themeId = Theme.Id()

    private var updatedTheme: Theme? = null
    private var result: Any? = null

    @Test
    fun `theme does not exist`() {
        whenSymbolIsAddedToTheme()
        result shouldBe themeDoesNotExist(themeId.uuid)
    }

    @Nested
    inner class `Valid Symbol Name` {

        val name = nonBlankStr(symbolName())

        init {
            givenThemeExists()
            whenSymbolIsAddedToTheme(name)
        }

        @Test
        fun `check symbol created correctly`() {
            val updatedTheme = updatedTheme!!
            assertEquals(themeId, updatedTheme.id)
            val createdSymbol = updatedTheme.symbols.single()
            assertEquals(name.value, createdSymbol.name)
        }

        @Test
        fun `check output`() {
            val actual = result as SymbolAddedToTheme
            assertEquals(themeId.uuid, actual.themeId)
            assertEquals(updatedTheme!!.symbols.single().id.uuid, actual.symbolId)
            assertEquals(name.value, actual.symbolName)
        }
    }

    @Test
    fun `add another symbol`() {
        val existingSymbolCount = 3
        givenThemeExists(existingSymbolCount)
        whenSymbolIsAddedToTheme(nonBlankStr("Valid name"))
        assertEquals(existingSymbolCount + 1, updatedTheme!!.symbols.size)
    }

    private val themeRepository = ThemeRepositoryDouble(onUpdateTheme = { updatedTheme = it })

    private fun givenThemeExists(symbolCount: Int = 0)
    {
        themeRepository.themes[themeId] = makeTheme(themeId, symbols = List(symbolCount) {
            Symbol("Symbol $it")
        })
    }

    private fun whenSymbolIsAddedToTheme(name: NonBlankString = nonBlankStr())
    {
        val useCase: AddSymbolToTheme = AddSymbolToThemeUseCase(themeRepository)
        val output = object : AddSymbolToTheme.OutputPort {
            override suspend fun addedSymbolToTheme(response: SymbolAddedToTheme) {
                result = response
            }
        }
        runBlocking {
            try { useCase.invoke(themeId.uuid, name, output) }
            catch (t: Throwable) { result = t }
        }
    }

}