package com.soyle.stories.usecase.scene.trackSymbolInScene

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.Updated
import com.soyle.stories.domain.theme.Symbol
import com.soyle.stories.usecase.scene.SceneRepository
import com.soyle.stories.usecase.theme.SymbolDoesNotExist
import com.soyle.stories.usecase.theme.ThemeRepository

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