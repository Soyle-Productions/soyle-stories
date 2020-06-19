package com.soyle.stories.theme.usecases

import com.soyle.stories.common.shouldBe
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.Symbol
import com.soyle.stories.theme.doubles.ThemeRepositoryDouble
import com.soyle.stories.theme.themeDoesNotExist
import com.soyle.stories.theme.usecases.addSymbolToTheme.AddSymbolToTheme
import com.soyle.stories.theme.usecases.addSymbolToTheme.AddSymbolToThemeUseCase
import com.soyle.stories.theme.usecases.addSymbolToTheme.SymbolAddedToTheme
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*

class AddSymbolToThemeUnitTest {

    private val themeId = Theme.Id()

    private var updatedTheme: Theme? = null
    private var result: Any? = null

    @Test
    fun `theme does not exist`() {
        whenSymbolIsAddedToTheme()
        result shouldBe themeDoesNotExist(themeId.uuid)
    }

    @Test
    fun `blank symbol name`() {
        givenThemeExists()
        whenSymbolIsAddedToTheme()
        result shouldBe ::symbolNameCannotBeBlank
    }

    @Nested
    inner class `Valid Symbol Name` {

        val name = "Valid Symbol Name ${UUID.randomUUID()}"

        init {
            givenThemeExists()
            whenSymbolIsAddedToTheme(name)
        }

        @Test
        fun `check symbol created correctly`() {
            val updatedTheme = updatedTheme!!
            assertEquals(themeId, updatedTheme.id)
            val createdSymbol = updatedTheme.symbols.single()
            assertEquals(name, createdSymbol.name)
        }

        @Test
        fun `check output`() {
            val actual = result as SymbolAddedToTheme
            assertEquals(themeId.uuid, actual.themeId)
            assertEquals(updatedTheme!!.symbols.single().id.uuid, actual.symbolId)
            assertEquals(name, actual.symbolName)
        }
    }

    @Test
    fun `add another symbol`() {
        val existingSymbolCount = 3
        givenThemeExists(existingSymbolCount)
        whenSymbolIsAddedToTheme("Valid name")
        assertEquals(existingSymbolCount + 1, updatedTheme!!.symbols.size)
    }

    private val themeRepository = ThemeRepositoryDouble(onUpdateTheme = { updatedTheme = it })

    private fun givenThemeExists(symbolCount: Int = 0)
    {
        themeRepository.themes[themeId] = Theme(themeId, Project.Id(), "", List(symbolCount) {
            Symbol("Symbol $it")
        }, "", mapOf(), mapOf())
    }

    private fun whenSymbolIsAddedToTheme(name: String = "")
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

    private fun symbolNameCannotBeBlank(actual: Any?)
    {
        actual as SymbolNameCannotBeBlank
    }

}