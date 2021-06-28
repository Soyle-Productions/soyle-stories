package com.soyle.stories.usecase.scene.symbol.listSymbolsInScene

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.scene.SceneRepository
import com.soyle.stories.usecase.theme.SymbolDoesNotExist
import com.soyle.stories.usecase.theme.ThemeRepository

class ListSymbolsInSceneUseCase(
    private val sceneRepository: SceneRepository,
    private val themeRepository: ThemeRepository
) : ListSymbolsInScene {

    override suspend fun invoke(sceneId: Scene.Id, output: ListSymbolsInScene.OutputPort) {
        val scene = sceneRepository.getSceneOrError(sceneId.uuid)
        if (scene.trackedSymbols.isEmpty()) {
            return output.receiveSymbolsInSceneList(ListSymbolsInScene.ResponseModel(listOf()))
        }
        val themes = themeRepository.getThemesContainingSymbols(scene.trackedSymbols.map { it.symbolId }.toSet())
        val symbolsInScene = scene.trackedSymbols.map {
            val theme = themes.getOrElse(it.symbolId) { throw SymbolDoesNotExist(it.symbolId.uuid) }
            ListSymbolsInScene.SymbolInScene(
                theme.id,
                theme.name,
                it.symbolId,
                it.symbolName,
                it.isPinned
            )
        }
        output.receiveSymbolsInSceneList(ListSymbolsInScene.ResponseModel(symbolsInScene))

    }
}