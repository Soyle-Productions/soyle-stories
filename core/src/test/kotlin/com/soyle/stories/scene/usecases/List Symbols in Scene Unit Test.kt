package com.soyle.stories.scene.usecases

import com.soyle.stories.common.mustEqual
import com.soyle.stories.doubles.ThemeRepositoryDouble
import com.soyle.stories.scene.SceneDoesNotExist
import com.soyle.stories.scene.doubles.SceneRepositoryDouble
import com.soyle.stories.scene.makeScene
import com.soyle.stories.scene.usecases.listSymbolsInScene.ListSymbolsInScene
import com.soyle.stories.scene.usecases.listSymbolsInScene.ListSymbolsInSceneUseCase
import com.soyle.stories.theme.SymbolDoesNotExist
import com.soyle.stories.theme.makeSymbol
import com.soyle.stories.theme.makeTheme
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class `List Symbols in Scene Unit Test` {

    private val scene = makeScene()

    private var result: ListSymbolsInScene.ResponseModel? = null

    private val sceneRepository = SceneRepositoryDouble()
    private val themeRepository = ThemeRepositoryDouble()

    @Test
    fun `Scene doesn't exist`() {
        val error = assertThrows<SceneDoesNotExist> {
            listSymbolsInScene()
        }
        error.sceneId.mustEqual(scene.id.uuid)
    }

    @Nested
    inner class `Given scene exists`
    {

        init {
            sceneRepository.givenScene(scene)
        }

        @Test
        fun `should produce empty output`() {
            listSymbolsInScene()
            assertTrue(result!!.isEmpty()) { "output is not empty" }
        }

        @Nested
        inner class `Given scene has symbols`
        {

            private val symbol = makeSymbol()
            private val theme = makeTheme(symbols = listOf(symbol))

            init {
                sceneRepository.givenScene(scene.withSymbolTracked(theme, symbol).scene)
            }

            @Test
            fun `symbol doesn't exist`() {
                val error = assertThrows<SymbolDoesNotExist> {
                    listSymbolsInScene()
                }
                error.symbolId.mustEqual(symbol.id.uuid)
            }

            @Nested
            inner class `Given symbols exist`
            {

                private val symbols = List(8) { makeSymbol() } + symbol
                private val themes = List(5) { makeTheme(symbols = listOf(symbols[it], makeSymbol())) } + makeTheme(symbols = symbols.takeLast(4))
                private val pinnedSymbols = symbols.withIndex().partition { it.index % 2 == 0 }.first.map { it.value }
                private val unpinnedSymbols = symbols.withIndex().partition { it.index % 2 == 0 }.second.map { it.value }

                init {
                    pinnedSymbols.fold(scene) { nextScene, pinnedSymbol ->
                        nextScene.withSymbolTracked(makeTheme(symbols = listOf(pinnedSymbol)), pinnedSymbol, true).scene
                    }.let {
                        unpinnedSymbols.fold(it) { nextScene, unpinnedSymbol ->
                            nextScene.withSymbolTracked(makeTheme(symbols = listOf(unpinnedSymbol)), unpinnedSymbol).scene
                        }
                    }.let(sceneRepository::givenScene)
                    themes.forEach(themeRepository::givenTheme)
                }

                @Test
                fun `should output symbol with theme`() {
                    listSymbolsInScene()
                    with(result!!) {
                        size.mustEqual(9)
                        map { it.symbolId }.toSet().mustEqual(symbols.map { it.id }.toSet())
                        map { it.themeId }.toSet().mustEqual(themes.map { it.id }.toSet())
                        forEach { symbolInScene ->
                            symbolInScene.symbolName.mustEqual(symbols.find { it.id == symbolInScene.symbolId }!!.name)
                        }
                        forEach { symbolInScene ->
                            symbolInScene.isPinned.mustEqual(pinnedSymbols.any { it.id == symbolInScene.symbolId })
                        }
                    }
                }

            }

        }

    }

    private fun listSymbolsInScene()
    {
        val useCase: ListSymbolsInScene = ListSymbolsInSceneUseCase(sceneRepository, themeRepository)
        val output = object : ListSymbolsInScene.OutputPort {
            override suspend fun receiveSymbolsInSceneList(response: ListSymbolsInScene.ResponseModel) {
                result = response
            }
        }
        runBlocking {
            useCase.invoke(scene.id, output)
        }
    }

}