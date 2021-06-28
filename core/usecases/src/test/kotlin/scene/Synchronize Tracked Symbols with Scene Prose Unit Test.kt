package com.soyle.stories.usecase.scene

import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.prose.ProseMention
import com.soyle.stories.domain.prose.ProseMentionRange
import com.soyle.stories.domain.prose.makeProse
import com.soyle.stories.domain.prose.mentioned
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.events.SymbolTrackedInScene
import com.soyle.stories.domain.scene.events.TrackedSymbolRemoved
import com.soyle.stories.domain.scene.makeScene
import com.soyle.stories.domain.theme.makeSymbol
import com.soyle.stories.domain.theme.makeTheme
import com.soyle.stories.usecase.prose.ProseDoesNotExist
import com.soyle.stories.usecase.repositories.ProseRepositoryDouble
import com.soyle.stories.usecase.repositories.SceneRepositoryDouble
import com.soyle.stories.usecase.repositories.ThemeRepositoryDouble
import com.soyle.stories.usecase.scene.symbol.trackSymbolInScene.SynchronizeTrackedSymbolsWithProse
import com.soyle.stories.usecase.scene.symbol.trackSymbolInScene.SynchronizeTrackedSymbolsWithProseUseCase
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

    }

    @Nested
    inner class `Given prose exists` {

        init {
            sceneRepository.givenScene(scene)
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

    }

    @Nested
    inner class `Given scene already tracks symbol` {

        init {
            proseRepository.givenProse(prose)
            themeRepository.givenTheme(theme)
            sceneRepository.givenScene(scene.withSymbolTracked(theme, symbol).scene)
        }

        @Test
        fun `should not update scene or output event`() {
            trackSymbolInScene()
            assertNull(updatedScene)
            assertNull(result)
        }

    }

    @Nested
    inner class `Given prose no longer mentions symbol` {

        init {
            themeRepository.givenTheme(theme)
            sceneRepository.givenScene(scene.withSymbolTracked(theme, symbol).scene)
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

    @Nested
    inner class `Given symbol is pinned in scene`
    {

        init {
            themeRepository.givenTheme(theme)
            sceneRepository.givenScene(scene.withSymbolTracked(theme, symbol).scene.withSymbolPinned(symbol.id).scene)
            proseRepository.givenProse(prose.withContentReplaced(listOf()).prose)
        }

        @Test
        fun `should not update scene or output event`() {
            trackSymbolInScene()
            assertNull(updatedScene)
            assertNull(result)
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