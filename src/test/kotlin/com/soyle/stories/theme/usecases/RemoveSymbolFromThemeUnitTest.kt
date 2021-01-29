package com.soyle.stories.theme.usecases

import com.soyle.stories.common.mustEqual
import com.soyle.stories.common.shouldBe
import com.soyle.stories.doubles.ThemeRepositoryDouble
import com.soyle.stories.entities.Scene
import com.soyle.stories.entities.Theme
import com.soyle.stories.scene.doubles.SceneRepositoryDouble
import com.soyle.stories.scene.makeScene
import com.soyle.stories.theme.SymbolDoesNotExist
import com.soyle.stories.theme.makeSymbol
import com.soyle.stories.theme.makeTheme
import com.soyle.stories.theme.symbolDoesNotExist
import com.soyle.stories.theme.usecases.removeSymbolFromTheme.RemoveSymbolFromTheme
import com.soyle.stories.theme.usecases.removeSymbolFromTheme.RemoveSymbolFromThemeUseCase
import com.soyle.stories.theme.usecases.removeSymbolFromTheme.SymbolRemovedFromTheme
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class RemoveSymbolFromThemeUnitTest {

    private val symbol = makeSymbol()
    private val theme = makeTheme(symbols = listOf(symbol))

    private var result: RemoveSymbolFromTheme.ResponseModel? = null

    private var updatedTheme: Theme? = null
    private val updatedScenes: MutableList<Scene> = mutableListOf()

    private val themeRepository = ThemeRepositoryDouble(onUpdateTheme = ::updatedTheme::set)
    private val sceneRepository = SceneRepositoryDouble(onUpdateScene = updatedScenes::add)

    @Test
    fun `symbol does not exist`() {
        val error = assertThrows<SymbolDoesNotExist> {
            removeSymbolFromTheme()
        }
        assertNull(updatedTheme)
        error shouldBe symbolDoesNotExist(symbol.id.uuid)
    }

    @Nested
    inner class `Given symbol exists`
    {

        init {
            themeRepository.givenTheme(theme)
        }

        @Test
        fun `should update theme to not have symbol`() {
            removeSymbolFromTheme()
            assertEquals(theme.withoutSymbol(symbol.id), updatedTheme!!)
        }

        @Test
        fun `should output symbol removed event`() {
            removeSymbolFromTheme()
            result!!.symbolRemovedFromTheme shouldBe ::symbolRemovedFromTheme
        }

        @Nested
        inner class `Given symbol tracked in scenes`
        {

            private val scenesWithSymbol = List(5) { makeScene().withSymbolTracked(theme, symbol).scene }
            private val scenesWithoutSymbol = List(4) { makeScene() }

            init {
                scenesWithSymbol.forEach(sceneRepository::givenScene)
                scenesWithoutSymbol.forEach(sceneRepository::givenScene)
            }

            @Test
            fun `should update scenes to not track symbol`() {
                removeSymbolFromTheme()
                updatedScenes.size.mustEqual(scenesWithSymbol.size)
                updatedScenes.map { it.id }.toSet().mustEqual(scenesWithSymbol.map { it.id }.toSet())
                updatedScenes.forEach { assertFalse(it.trackedSymbols.isSymbolTracked(symbol.id)) }
            }

            @Test
            fun `should output tracked symbol removed events`() {
                removeSymbolFromTheme()
                result!!.trackedSymbolsRemoved.size.mustEqual(scenesWithSymbol.size)
                result!!.trackedSymbolsRemoved.map { it.sceneId }.toSet().mustEqual(scenesWithSymbol.map { it.id }.toSet())
                result!!.trackedSymbolsRemoved.forEach { it.trackedSymbol.themeId.mustEqual(theme.id) }
                result!!.trackedSymbolsRemoved.forEach { it.trackedSymbol.symbolId.mustEqual(symbol.id) }
                result!!.trackedSymbolsRemoved.forEach { it.trackedSymbol.symbolName.mustEqual(symbol.name) }
            }

        }

    }

    private fun removeSymbolFromTheme()
    {
        val useCase: RemoveSymbolFromTheme = RemoveSymbolFromThemeUseCase(themeRepository, sceneRepository)
        val output = object : RemoveSymbolFromTheme.OutputPort {
            override suspend fun removedSymbolFromTheme(response: RemoveSymbolFromTheme.ResponseModel) {
                result = response
            }
        }
        runBlocking {
            useCase.invoke(symbol.id.uuid, output)
        }
    }

    private fun symbolRemovedFromTheme(actual: Any?)
    {
        actual as SymbolRemovedFromTheme
        assertEquals(updatedTheme!!.id.uuid, actual.themeId)
        assertEquals(symbol.id.uuid, actual.symbolId)
        assertEquals(symbol.name, actual.symbolName)
    }

}