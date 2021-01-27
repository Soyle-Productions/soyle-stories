package com.soyle.stories.theme.usecases

import com.soyle.stories.common.mustEqual
import com.soyle.stories.common.shouldBe
import com.soyle.stories.doubles.ThemeRepositoryDouble
import com.soyle.stories.entities.Scene
import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.Symbol
import com.soyle.stories.scene.doubles.SceneRepositoryDouble
import com.soyle.stories.scene.makeScene
import com.soyle.stories.theme.*
import com.soyle.stories.theme.usecases.renameSymbol.RenameSymbol
import com.soyle.stories.theme.usecases.renameSymbol.RenameSymbolUseCase
import com.soyle.stories.theme.usecases.renameSymbol.RenamedSymbol
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class RenameSymbolUnitTest {

    private val themeId = Theme.Id()
    private val symbol = makeSymbol()

    private var result: RenameSymbol.ResponseModel? = null

    private var updatedTheme: Theme? = null
    private var updatedScenes: MutableList<Scene> = mutableListOf()

    private val themeRepository = ThemeRepositoryDouble(onUpdateTheme = { updatedTheme = it })
    private val sceneRepository = SceneRepositoryDouble(onUpdateScene = updatedScenes::add)

    @Test
    fun `new name is invalid`() {
        assertThrows<SymbolNameCannotBeBlank> {
            renameSymbol()
        }
        assertNull(updatedTheme)
    }

    @Test
    fun `symbol does not exist`() {
        val error = assertThrows<SymbolDoesNotExist> {
            renameSymbol("Not blank name")
        }
        assertNull(updatedTheme)
        error shouldBe symbolDoesNotExist(symbol.id.uuid)
    }

    @Test
    fun `symbol has same name`() {
        val sameName = "Pre-existing name"
        themeRepository.themes[themeId] = makeTheme(themeId, symbols = listOf(
            symbol.withName(sameName)
        ))
        val error = assertThrows<SymbolAlreadyHasName> {
            renameSymbol(sameName)
        }
        assertNull(updatedTheme)
        error shouldBe symbolAlreadyHasName(symbol.id.uuid, sameName)
    }

    @Test
    fun `input name is valid and different`() {
        val newName = "Valid New Name"
        val theme = makeTheme(themeId, symbols = listOf(
            Symbol(symbol.id, "Different Name")
        ))
        themeRepository.themes[themeId] = theme
        renameSymbol(newName)
        assertNotNull(updatedTheme)
        assertEquals(theme.withoutSymbol(symbol.id).withSymbol(Symbol(symbol.id, newName)), updatedTheme!!)
        result!!.renamedSymbol shouldBe renamedSymbol(newName)
    }

    @Nested
    inner class `Symbol tracked in scenes`
    {

        private val scenesWithSymbol = List(5) {
            makeScene(symbols = listOf(Scene.TrackedSymbol(symbol.id, symbol.name)))
        }
        private val scenesWithoutSymbol = List(4) {
            makeScene()
        }

        init {
            themeRepository.givenTheme(makeTheme(symbols = listOf(symbol)))
            scenesWithSymbol.forEach(sceneRepository::givenScene)
            scenesWithoutSymbol.forEach(sceneRepository::givenScene)
        }

        @Test
        fun `should update scenes with symbol`() {
            renameSymbol("Non blank name")
            updatedScenes.size.mustEqual(scenesWithSymbol.size)
            updatedScenes.map { it.id }.toSet().mustEqual(scenesWithSymbol.map { it.id }.toSet())
            updatedScenes.forEach { it.trackedSymbols.getSymbolById(symbol.id)!!.symbolName.mustEqual("Non blank name") }
        }

        @Test
        fun `should output renamed tracked symbol events`() {
            renameSymbol("Non blank name")
            result!!.trackedSymbolsRenamed.let {
                it.size.mustEqual(scenesWithSymbol.size)
                it.map { it.sceneId }.toSet().mustEqual(scenesWithSymbol.map { it.id }.toSet())
                it.forEach { it.trackedSymbol.symbolId.mustEqual(symbol.id) }
            }
        }
    }

    private fun renameSymbol(name: String = "") {
        val useCase: RenameSymbol = RenameSymbolUseCase(themeRepository, sceneRepository)
        val output = object : RenameSymbol.OutputPort {
            override suspend fun symbolRenamed(response: RenameSymbol.ResponseModel) {
                result = response
            }
        }
        runBlocking {
            useCase.invoke(symbol.id.uuid, name, output)
        }
    }

    private fun renamedSymbol(expectedName: String): (Any?) -> Unit = { actual ->
        actual as RenamedSymbol
        assertEquals(themeId.uuid, actual.themeId)
        assertEquals(symbol.id.uuid, actual.symbolId)
        assertEquals(expectedName, actual.newName)
    }

}