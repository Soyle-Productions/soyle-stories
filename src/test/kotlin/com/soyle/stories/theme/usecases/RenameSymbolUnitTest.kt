package com.soyle.stories.theme.usecases

import com.soyle.stories.common.shouldBe
import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.Symbol
import com.soyle.stories.theme.*
import com.soyle.stories.theme.doubles.ThemeRepositoryDouble
import com.soyle.stories.theme.usecases.renameSymbol.RenameSymbol
import com.soyle.stories.theme.usecases.renameSymbol.RenameSymbolUseCase
import com.soyle.stories.theme.usecases.renameSymbol.RenamedSymbol
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class RenameSymbolUnitTest {

    private val themeId = Theme.Id()
    private val symbolId = Symbol.Id()

    private var result: Any? = null
    private val output = object : RenameSymbol.OutputPort {
        override suspend fun symbolRenamed(response: RenamedSymbol) {
            result = response
        }
    }

    @Test
    fun `new name is invalid`() {
        assertThrows<SymbolNameCannotBeBlank> {
            renameSymbol()
        }
    }

    @Test
    fun `symbol does not exist`() {
        result = assertThrows<SymbolDoesNotExist> {
            renameSymbol("Not blank name")
        }
        result shouldBe symbolDoesNotExist(symbolId.uuid)
    }

    @Test
    fun `symbol has same name`() {
        val sameName = "Pre-existing name"
        repo.themes[themeId] = makeTheme(themeId, symbols = listOf(
            Symbol(symbolId, sameName)
        ))
        result = assertThrows<SymbolAlreadyHasName> {
            renameSymbol(sameName)
        }
        result shouldBe symbolAlreadyHasName(symbolId.uuid, sameName)
    }

    @Test
    fun `input name is valid and different`() {
        val newName = "Valid New Name"
        repo.themes[themeId] = makeTheme(themeId, symbols = listOf(
            Symbol(symbolId, "Different Name")
        ))
        renameSymbol(newName)
        result shouldBe renamedSymbol(newName)
    }

    val repo = ThemeRepositoryDouble()

    private fun renameSymbol(name: String = "") {
        val useCase: RenameSymbol = RenameSymbolUseCase(repo)
        runBlocking {
            useCase.invoke(symbolId.uuid, name, output)
        }
    }

    private fun renamedSymbol(expectedName: String): (Any?) -> Unit = { actual ->
        actual as RenamedSymbol
        assertEquals(themeId.uuid, actual.themeId)
        assertEquals(symbolId.uuid, actual.symbolId)
        assertEquals(expectedName, actual.newName)
    }

}