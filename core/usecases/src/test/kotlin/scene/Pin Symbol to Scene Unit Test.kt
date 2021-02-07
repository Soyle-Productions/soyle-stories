package com.soyle.stories.usecase.scene

import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.makeScene
import com.soyle.stories.domain.theme.makeSymbol
import com.soyle.stories.domain.theme.makeTheme
import com.soyle.stories.usecase.repositories.SceneRepositoryDouble
import com.soyle.stories.usecase.repositories.ThemeRepositoryDouble
import com.soyle.stories.usecase.scene.trackSymbolInScene.PinSymbolToScene
import com.soyle.stories.usecase.scene.trackSymbolInScene.PinSymbolToSceneUseCase
import com.soyle.stories.usecase.theme.SymbolDoesNotExist
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class `Pin Symbol to Scene Unit Test` {

    private val symbol = makeSymbol()
    private val theme = makeTheme(symbols = listOf(symbol))
    private val scene = makeScene()

    private var updatedScene: Scene? = null
    private var result: PinSymbolToScene.ResponseModel? = null

    private val sceneRepository = SceneRepositoryDouble(onUpdateScene = ::updatedScene::set)
    private val themeRepository = ThemeRepositoryDouble()

    @Test
    fun `scene doesn't exist`() {
        val error = assertThrows<SceneDoesNotExist> {
            pinSymbolToScene()
        }
        error.sceneId.mustEqual(scene.id.uuid)
    }

    @Nested
    inner class `Given scene exists`
    {

        init {
            sceneRepository.givenScene(scene)
        }

        @Nested
        inner class `Given symbol is tracked`
        {

            init {
                sceneRepository.givenScene(scene.withSymbolTracked(theme, symbol).scene)
            }

            @Test
            fun `should update scene`() {
                pinSymbolToScene()
                updatedScene!!.trackedSymbols.getSymbolByIdOrError(symbol.id).isPinned.mustEqual(true)
            }

            @Test
            fun `should output event`() {
                pinSymbolToScene()
                result!!.symbolPinnedToScene!!.let {
                    it.sceneId.mustEqual(scene.id)
                    it.trackedSymbol.mustEqual(updatedScene!!.trackedSymbols.getSymbolByIdOrError(symbol.id))
                }
            }

            @Nested
            inner class `Given symbol is already pinned`
            {

                init {
                    sceneRepository.givenScene(scene.withSymbolTracked(theme, symbol, true).scene)
                }

                @Test
                fun `should not update scene`() {
                    pinSymbolToScene()
                    Assertions.assertNull(updatedScene)
                }

                @Test
                fun `should not produce output`() {
                    pinSymbolToScene()
                    Assertions.assertNull(result)
                }

            }
        }

        @Test
        fun `symbol doesn't exist`() {
            val error = assertThrows<SymbolDoesNotExist> {
                pinSymbolToScene()
            }
            error.symbolId.mustEqual(symbol.id.uuid)
        }

        @Nested
        inner class `Given symbol exists`
        {

            init {
                themeRepository.givenTheme(theme)
            }

            @Test
            fun `should track symbol in scene`() {
                pinSymbolToScene()
                updatedScene!!.trackedSymbols.getSymbolByIdOrError(symbol.id).isPinned.mustEqual(true)
            }

            @Test
            fun `should output event`() {
                pinSymbolToScene()
                result!!.symbolTrackedInScene!!.let {
                    it.sceneId.mustEqual(scene.id)
                    it.trackedSymbol.mustEqual(updatedScene!!.trackedSymbols.getSymbolByIdOrError(symbol.id))
                }
            }

        }

    }

    private fun pinSymbolToScene() {
        val useCase: PinSymbolToScene = PinSymbolToSceneUseCase(sceneRepository, themeRepository)
        val output = object : PinSymbolToScene.OutputPort {
            override suspend fun symbolPinnedToScene(response: PinSymbolToScene.ResponseModel) {
                result = response
            }
        }
        runBlocking {
            useCase.invoke(scene.id, symbol.id, output)
        }
    }

}