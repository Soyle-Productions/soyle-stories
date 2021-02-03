package com.soyle.stories.scene.usecases

import com.soyle.stories.common.mustEqual
import com.soyle.stories.doubles.ThemeRepositoryDouble
import com.soyle.stories.scene.SceneDoesNotExist
import com.soyle.stories.scene.doubles.SceneRepositoryDouble
import com.soyle.stories.scene.makeScene
import com.soyle.stories.scene.usecases.trackSymbolInScene.ListAvailableSymbolsToTrackInScene
import com.soyle.stories.scene.usecases.trackSymbolInScene.ListAvailableSymbolsToTrackInSceneUseCase
import com.soyle.stories.theme.makeSymbol
import com.soyle.stories.theme.makeTheme
import com.soyle.stories.theme.usecases.listSymbolsByTheme.SymbolsByTheme
import com.soyle.stories.theme.usecases.listSymbolsByTheme.theme
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class `List Available Symbols to Track in Scene Unit Test` {

    private val scene = makeScene()

    private val sceneRepository = SceneRepositoryDouble()
    private val themeRepository = ThemeRepositoryDouble()

    private var result: SymbolsByTheme? = null

    @Test
    fun `scene doesn't exist`() {
        val error = assertThrows<SceneDoesNotExist> {
            listAvailableSymbolsToTrackInScene()
        }
        error.sceneId.mustEqual(scene.id.uuid)
    }

    @Nested
    inner class `Given Scene Exists` {
        init {
            sceneRepository.givenScene(scene)
        }

        @Test
        fun `should output empty response`() {
            listAvailableSymbolsToTrackInScene()
            result!!
        }

        @Nested
        inner class `Given symbols in project` {

            private val themeWithoutSymbol = makeTheme(projectId = scene.projectId)
            private val themesWithSymbols =
                List(4) { makeTheme(projectId = scene.projectId, symbols = List(it + 1) { makeSymbol() }) }

            init {
                (themesWithSymbols + themeWithoutSymbol).onEach(themeRepository::givenTheme)
            }

            @Test
            fun `should output available symbols by theme`() {
                listAvailableSymbolsToTrackInScene()
                result!!.themes.map { it.theme.themeId }.toSet().mustEqual(themesWithSymbols.map { it.id.uuid }.toSet())
                result!!.themes.forEach { (themeItem, symbolItems) ->
                    val backingTheme = themesWithSymbols.find { it.id.uuid == themeItem.themeId }!!
                    themeItem.themeName.mustEqual(backingTheme.name)
                    symbolItems.map { it.symbolId }.toSet().mustEqual(backingTheme.symbols.map { it.id.uuid }.toSet())
                    symbolItems.forEach { symbolItem ->
                        symbolItem.symbolName.mustEqual(backingTheme.symbols.find { it.id.uuid == symbolItem.symbolId }!!.name)
                    }
                }
            }

            @Nested
            inner class `Given scene contains symbols` {
                init {
                    themesWithSymbols.fold(scene) { nextScene, theme ->
                        nextScene.withSymbolTracked(theme, theme.symbols.first()).scene
                    }.let(sceneRepository::givenScene)
                }

                @Test
                fun `should filter out symbols already tracked`() {
                    listAvailableSymbolsToTrackInScene()
                    result!!.themes.size.mustEqual(themesWithSymbols.size - 1)
                }

            }

        }
    }

    private fun listAvailableSymbolsToTrackInScene() {
        val useCase: ListAvailableSymbolsToTrackInScene =
            ListAvailableSymbolsToTrackInSceneUseCase(sceneRepository, themeRepository)
        val output = object : ListAvailableSymbolsToTrackInScene.OutputPort {
            override suspend fun receiveAvailableSymbolsToTrackInScene(response: SymbolsByTheme) {
                result = response
            }
        }
        runBlocking {
            useCase.invoke(scene.id, output)
        }
    }
}