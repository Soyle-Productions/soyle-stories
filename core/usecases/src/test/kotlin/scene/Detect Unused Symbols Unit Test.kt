package com.soyle.stories.usecase.scene

import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.prose.makeProse
import com.soyle.stories.domain.prose.mentioned
import com.soyle.stories.domain.scene.makeScene
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.theme.makeSymbol
import com.soyle.stories.domain.theme.makeTheme
import com.soyle.stories.usecase.prose.ProseDoesNotExist
import com.soyle.stories.usecase.repositories.ProseRepositoryDouble
import com.soyle.stories.usecase.repositories.SceneRepositoryDouble
import com.soyle.stories.usecase.scene.trackSymbolInScene.DetectUnusedSymbolsInScene
import com.soyle.stories.usecase.scene.trackSymbolInScene.DetectUnusedSymbolsInSceneUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class `Detect Unused Symbols Unit Test` {

    private val scene = makeScene()

    private val prose = makeProse(id = scene.proseId)

    private val sceneRepository = SceneRepositoryDouble()
    private val proseRepository = ProseRepositoryDouble()

    private var result: DetectUnusedSymbolsInScene.ResponseModel? = null

    @Test
    fun `scene doesn't exist`() {
        val error = assertThrows<SceneDoesNotExist> {
            detectUnusedSymbols()
        }
        error.sceneId.mustEqual(scene.id.uuid)
    }

    @Nested
    inner class `Given scene exists` {

        init {
            sceneRepository.givenScene(scene)
        }

        @Test
        fun `prose doesn't exist`() {
            val error = assertThrows<ProseDoesNotExist> {
                detectUnusedSymbols()
            }
            error.proseId.mustEqual(prose.id)
        }

        @Nested
        inner class `Given prose exists` {

            init {
                proseRepository.givenProse(prose)
            }

            @Test
            fun `should output empty result`() {
                detectUnusedSymbols()
                assertEquals(scene.id, result!!.sceneId)
                assertTrue(result!!.unusedSymbolIds.isEmpty())
            }

            @Nested
            inner class `Given symbols not mentioned in prose` {

                private val symbolsNotMentioned = List(3) { makeSymbol() }
                private val symbolMentioned = makeSymbol()

                init {
                    prose.withTextInserted(symbolMentioned.name)
                        .prose.withEntityMentioned(
                            symbolMentioned.id.mentioned(Theme.Id()),
                            0,
                            symbolMentioned.name.length
                        )
                        .prose.let { proseRepository.givenProse(it) }
                    (symbolsNotMentioned + symbolMentioned).fold(scene) { nextScene, symbol ->
                        nextScene.withSymbolTracked(makeTheme(symbols = listOf(symbol)), symbol, true).scene
                    }.let { sceneRepository.givenScene(it) }
                }

                @Test
                fun `should output symbol ids`() {
                    detectUnusedSymbols()
                    assertEquals(scene.id, result!!.sceneId)
                    assertEquals(symbolsNotMentioned.map { it.id }.toSet(), result!!.unusedSymbolIds)
                }

            }

        }

    }

    private fun detectUnusedSymbols() {
        val useCase: DetectUnusedSymbolsInScene = DetectUnusedSymbolsInSceneUseCase(sceneRepository, proseRepository)
        val output = object : DetectUnusedSymbolsInScene.OutputPort {
            override suspend fun receiveDetectedUnusedSymbols(response: DetectUnusedSymbolsInScene.ResponseModel) {
                result = response
            }
        }
        runBlocking {
            useCase.invoke(scene.id, output)
        }
    }

}