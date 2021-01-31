package com.soyle.stories.scene.usecases

import com.soyle.stories.common.mustEqual
import com.soyle.stories.entities.Scene
import com.soyle.stories.scene.SceneDoesNotExist
import com.soyle.stories.scene.doubles.SceneRepositoryDouble
import com.soyle.stories.scene.makeScene
import com.soyle.stories.scene.usecases.trackSymbolInScene.PinSymbolToScene
import com.soyle.stories.scene.usecases.trackSymbolInScene.PinSymbolToSceneUseCase
import com.soyle.stories.theme.makeSymbol
import com.soyle.stories.theme.makeTheme
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class `Pin Symbol to Scene Unit Test` {

    private val symbol = makeSymbol()
    private val theme = makeTheme(symbols = listOf(symbol))
    private val scene = makeScene()
        .withSymbolTracked(theme, symbol).scene

    private var updatedScene: Scene? = null
    private var result: PinSymbolToScene.ResponseModel? = null

    private val sceneRepository = SceneRepositoryDouble(onUpdateScene = ::updatedScene::set)

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

        @Test
        fun `should update scene`() {
            pinSymbolToScene()
            updatedScene!!.trackedSymbols.getSymbolByIdOrError(symbol.id).isPinned.mustEqual(true)
        }

        @Test
        fun `should output event`() {
            pinSymbolToScene()
            result!!.symbolPinnedToScene.let {
                it.sceneId.mustEqual(scene.id)
                it.trackedSymbol.mustEqual(updatedScene!!.trackedSymbols.getSymbolByIdOrError(symbol.id))
            }
        }

        @Nested
        inner class `Given symbol is already pinned`
        {

            init {
                sceneRepository.givenScene(scene.withSymbolPinned(symbol.id).scene)
            }

            @Test
            fun `should not update scene`() {
                pinSymbolToScene()
                assertNull(updatedScene)
            }

            @Test
            fun `should not produce output`() {
                pinSymbolToScene()
                assertNull(result)
            }

        }

    }

    private fun pinSymbolToScene() {
        val useCase: PinSymbolToScene = PinSymbolToSceneUseCase(sceneRepository)
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