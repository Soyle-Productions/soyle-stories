package com.soyle.stories.scene.usecases.trackSymbolInScene

import com.soyle.stories.entities.Scene
import com.soyle.stories.entities.Updated
import com.soyle.stories.entities.theme.Symbol
import com.soyle.stories.scene.repositories.SceneRepository
import com.soyle.stories.scene.repositories.getSceneOrError
import com.soyle.stories.theme.SymbolDoesNotExist
import com.soyle.stories.theme.repositories.ThemeRepository

class PinSymbolToSceneUseCase(
    private val sceneRepository: SceneRepository,
    private val themeRepository: ThemeRepository
) : PinSymbolToScene {
    override suspend fun invoke(sceneId: Scene.Id, symbolId: Symbol.Id, output: PinSymbolToScene.OutputPort) {
        val scene = sceneRepository.getSceneOrError(sceneId.uuid)
        if (scene.trackedSymbols.isSymbolTracked(symbolId)) {
            val sceneUpdate = scene.withSymbolPinned(symbolId)
            if (sceneUpdate is Updated) {
                sceneRepository.updateScene(sceneUpdate.scene)
                output.symbolPinnedToScene(PinSymbolToScene.ResponseModel(sceneUpdate.event, null))
            }
        } else {
            val theme = themeRepository.getThemeContainingSymbolWithId(symbolId)
                ?: throw SymbolDoesNotExist(symbolId.uuid)
            val symbol = theme.symbols.find { it.id == symbolId }!!
            val sceneUpdate = scene.withSymbolTracked(theme, symbol, true)
            if (sceneUpdate is Updated) {
                sceneRepository.updateScene(sceneUpdate.scene)
                output.symbolPinnedToScene(PinSymbolToScene.ResponseModel(null, sceneUpdate.event))
            }
        }
    }
}