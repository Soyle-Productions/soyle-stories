package com.soyle.stories.scene.usecases

import com.soyle.stories.common.mustEqual
import com.soyle.stories.common.singleLine
import com.soyle.stories.doubles.ProseRepositoryDouble
import com.soyle.stories.entities.ProseContent
import com.soyle.stories.entities.Scene
import com.soyle.stories.entities.mentioned
import com.soyle.stories.prose.makeProse
import com.soyle.stories.scene.SceneDoesNotExist
import com.soyle.stories.scene.doubles.SceneRepositoryDouble
import com.soyle.stories.scene.makeScene
import com.soyle.stories.scene.usecases.trackSymbolInScene.UnpinSymbolFromScene
import com.soyle.stories.scene.usecases.trackSymbolInScene.UnpinSymbolFromSceneUseCase
import com.soyle.stories.theme.makeSymbol
import com.soyle.stories.theme.makeTheme
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class `Unpin Symbol From Scene Unit Test` {

    private val symbol = makeSymbol()
    private val theme = makeTheme(symbols = listOf(symbol))
    private val scene = makeScene()
        .withSymbolTracked(theme, symbol, true)
        .scene
    private val prose = makeProse(id = scene.proseId)
        .withContentReplaced(listOf(
            ProseContent("", symbol.id.mentioned(theme.id) to singleLine(symbol.name))
        )).prose

    private var updatedScene: Scene? = null
    private var result: UnpinSymbolFromScene.ResponseModel? = null

    private val sceneRepository = SceneRepositoryDouble(onUpdateScene = ::updatedScene::set)
    private val proseRepository = ProseRepositoryDouble()

    @Test
    fun `scene doesn't exist`() {
        val error = assertThrows<SceneDoesNotExist> {
            unpinSymbolFromScene()
        }
        error.sceneId.mustEqual(scene.id.uuid)
    }

    @Nested
    inner class `Given Scene Exists`
    {

        init {
            sceneRepository.givenScene(scene)
            proseRepository.givenProse(prose)
        }

        @Test
        fun `should update scene to not have symbol pinned`() {
            unpinSymbolFromScene()
            updatedScene!!.trackedSymbols.getSymbolByIdOrError(symbol.id).isPinned.mustEqual(false)
        }

        @Test
        fun `should output unpinned symbol event`() {
            unpinSymbolFromScene()
            result!!.symbolUnpinnedFromScene!!.trackedSymbol.isPinned.mustEqual(false)
        }

        @Nested
        inner class `Given symbol already unpinned`
        {
            init {
                sceneRepository.givenScene(scene.withSymbolUnpinned(symbol.id).scene)
            }

            @Test
            fun `should not update scene or receive output`() {
                unpinSymbolFromScene()
                assertNull(updatedScene)
                assertNull(result)
            }
        }

        @Nested
        inner class `Given symbol is not mentioned in prose`
        {

            init {
                proseRepository.givenProse(prose.withContentReplaced(listOf()).prose)
            }

            @Test
            fun `should remove tracked symbol from scene`() {
                unpinSymbolFromScene()
                updatedScene!!.trackedSymbols.isSymbolTracked(symbol.id).mustEqual(false)
            }

            @Test
            fun `should output removed symbol event`() {
                unpinSymbolFromScene()
                result!!.trackedSymbolRemoved!!.trackedSymbol.symbolId.mustEqual(symbol.id)
            }

        }

    }

    private fun unpinSymbolFromScene()
    {
        val useCase: UnpinSymbolFromScene = UnpinSymbolFromSceneUseCase(sceneRepository, proseRepository)
        val output = object : UnpinSymbolFromScene.OutputPort {
            override suspend fun symbolUnpinnedFromScene(response: UnpinSymbolFromScene.ResponseModel) {
                result = response
            }
        }
        runBlocking {
            useCase.invoke(scene.id, symbol.id, output)
        }
    }

}