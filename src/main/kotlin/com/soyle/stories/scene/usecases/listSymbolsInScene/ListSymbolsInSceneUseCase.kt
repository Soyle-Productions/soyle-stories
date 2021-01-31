package com.soyle.stories.scene.usecases.listSymbolsInScene

import com.soyle.stories.entities.Scene
import com.soyle.stories.scene.repositories.SceneRepository
import com.soyle.stories.scene.repositories.getSceneOrError
import com.soyle.stories.theme.SymbolDoesNotExist
import com.soyle.stories.theme.repositories.ThemeRepository

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