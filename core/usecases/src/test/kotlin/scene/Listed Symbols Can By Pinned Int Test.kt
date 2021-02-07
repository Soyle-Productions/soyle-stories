package com.soyle.stories.usecase.scene

import com.soyle.stories.domain.theme.Symbol
import com.soyle.stories.usecase.repositories.ThemeRepositoryDouble
import com.soyle.stories.domain.scene.makeScene
import com.soyle.stories.usecase.scene.trackSymbolInScene.ListAvailableSymbolsToTrackInScene
import com.soyle.stories.usecase.scene.trackSymbolInScene.ListAvailableSymbolsToTrackInSceneUseCase
import com.soyle.stories.usecase.scene.trackSymbolInScene.PinSymbolToScene
import com.soyle.stories.usecase.scene.trackSymbolInScene.PinSymbolToSceneUseCase
import com.soyle.stories.domain.theme.makeSymbol
import com.soyle.stories.domain.theme.makeTheme
import com.soyle.stories.usecase.theme.listSymbolsByTheme.SymbolsByTheme
import com.soyle.stories.usecase.repositories.SceneRepositoryDouble
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class `Listed Symbols Can By Pinned Int Test` {

    private val scene = makeScene()
    private val themesWithSymbols =
        List(4) { makeTheme(projectId = scene.projectId, symbols = List(it + 1) { makeSymbol() }) }


    private val sceneRepository = SceneRepositoryDouble()
        .apply { givenScene(scene) }
    private val themeRepository = ThemeRepositoryDouble()
        .apply { themesWithSymbols.forEach(this::givenTheme) }


    private val listAvailableSymbolsToTrackInScene = ListAvailableSymbolsToTrackInSceneUseCase(
        sceneRepository, themeRepository
    )
    private val pinSymbolToScene = PinSymbolToSceneUseCase(
        sceneRepository,
        themeRepository
    )

    private var result: PinSymbolToScene.ResponseModel? = null

    @Test
    fun `should pin new symbol`() {
        val pinSymbolToSceneOutput = object : PinSymbolToScene.OutputPort {
            override suspend fun symbolPinnedToScene(response: PinSymbolToScene.ResponseModel) {
                result = response
            }
        }
        val listAvailableSymbolsToTrackInSceneOutput = object : ListAvailableSymbolsToTrackInScene.OutputPort {
            override suspend fun receiveAvailableSymbolsToTrackInScene(response: SymbolsByTheme) {
                pinSymbolToScene(
                    scene.id,
                    response.themes.random().second.random().symbolId.let(Symbol::Id),
                    pinSymbolToSceneOutput
                )
            }
        }
        runBlocking {
            listAvailableSymbolsToTrackInScene(scene.id, listAvailableSymbolsToTrackInSceneOutput)
        }
        result!!.symbolTrackedInScene!!
    }

}