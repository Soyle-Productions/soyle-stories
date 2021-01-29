package com.soyle.stories.scene.usecases

import com.soyle.stories.common.mustEqual
import com.soyle.stories.doubles.ProseRepositoryDouble
import com.soyle.stories.doubles.ThemeRepositoryDouble
import com.soyle.stories.entities.*
import com.soyle.stories.prose.ProseDoesNotExist
import com.soyle.stories.prose.makeProse
import com.soyle.stories.scene.doubles.SceneRepositoryDouble
import com.soyle.stories.scene.makeScene
import com.soyle.stories.scene.usecases.trackSymbolInScene.SynchronizeTrackedSymbolsWithProse
import com.soyle.stories.scene.usecases.trackSymbolInScene.SynchronizeTrackedSymbolsWithProseUseCase
import com.soyle.stories.theme.makeSymbol
import com.soyle.stories.theme.makeTheme
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class `Synchronize Tracked Symbols with Scene Prose Unit Test` {

    private val scene = makeScene()
    private val symbol = makeSymbol()
    private val theme = makeTheme(symbols = listOf(symbol))
    private val prose = makeProse(
        id = scene.proseId,
        content = symbol.name,
        mentions = listOf(ProseMention(symbol.id.mentioned(theme.id), ProseMentionRange(0, symbol.name.length)))
    )

    private var updatedScene: Scene? = null
    private var result: SynchronizeTrackedSymbolsWithProse.ResponseModel? = null

    private val sceneRepository = SceneRepositoryDouble(onUpdateScene = ::updatedScene::set)
    private val proseRepository = ProseRepositoryDouble()
    private val themeRepository = ThemeRepositoryDouble()

    @Test
    fun `fail silently when no scene owns prose`() {
        trackSymbolInScene()
        assertNull(updatedScene)
        assertNull(result)
    }

    @Nested
    inner class `Given scene owns prose` {

        init {
            sceneRepository.givenScene(scene)
        }

        @Test
        fun `prose doesn't exist`() {
            val error = assertFailsWith<ProseDoesNotExist> {
                trackSymbolInScene()
            }
            error.proseId.mustEqual(prose.id)
        }

        @Nested
        inner class `Given prose exists` {

            init {
                proseRepository.givenProse(prose)
                themeRepository.givenTheme(theme)
            }

            @Test
            fun `should add symbol to scene`() {
                trackSymbolInScene()
                updatedScene!!.let {
                    it.trackedSymbols.isSymbolTracked(symbol.id).mustEqual(true)
                }
            }

            @Test
            fun `should output event`() {
                trackSymbolInScene()
                result!!.let {
                    it.symbolsTrackedInScene.mustEqual(
                        listOf(
                            SymbolTrackedInScene(
                                scene.id,
                                theme.name,
                                Scene.TrackedSymbol(symbol.id, symbol.name, theme.id)
                            )
                        )
                    )
                }
            }

            @Nested
            inner class `Given scene already tracks symbol` {

                init {
                    sceneRepository.givenScene(scene.withSymbolTracked(theme, symbol).scene)
                }

                @Test
                fun `should not update scene or output event`() {
                    trackSymbolInScene()
                    assertNull(updatedScene)
                    assertNull(result)
                }

                @Nested
                inner class `Given prose no longer mentions symbol` {

                    init {
                        proseRepository.givenProse(prose.withContentReplaced(listOf()).prose)
                    }

                    @Test
                    fun `should remove symbol from scene`() {
                        trackSymbolInScene()
                        updatedScene!!.let {
                            it.trackedSymbols.isSymbolTracked(symbol.id).mustEqual(false)
                        }
                    }

                    @Test
                    fun `should output symbol removed event`() {
                        trackSymbolInScene()
                        result!!.let {
                            assertTrue(it.symbolsTrackedInScene.isEmpty())
                            it.symbolsNoLongerTrackedInScene.mustEqual(listOf(
                                TrackedSymbolRemoved(scene.id, Scene.TrackedSymbol(symbol.id, symbol.name, theme.id))
                            ))
                        }
                    }

                }

            }

        }

    }

    private fun trackSymbolInScene() {
        val useCase: SynchronizeTrackedSymbolsWithProse =
            SynchronizeTrackedSymbolsWithProseUseCase(sceneRepository, proseRepository, themeRepository)
        val output = object : SynchronizeTrackedSymbolsWithProse.OutputPort {
            override suspend fun symbolTrackedInScene(response: SynchronizeTrackedSymbolsWithProse.ResponseModel) {
                result = response
            }
        }
        runBlocking {
            useCase.invoke(scene.proseId, output)
        }
    }

    private inline fun <reified T : Throwable> assertFailsWith(crossinline block: () -> Unit): T {
        val error = assertThrows<T> {
            block()
        }
        assertNull(updatedScene)
        assertNull(result)
        return error
    }

}