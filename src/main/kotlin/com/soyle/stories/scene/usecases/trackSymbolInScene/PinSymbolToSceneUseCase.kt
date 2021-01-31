package com.soyle.stories.scene.usecases.trackSymbolInScene

import com.soyle.stories.entities.Scene
import com.soyle.stories.entities.Updated
import com.soyle.stories.entities.theme.Symbol
import com.soyle.stories.scene.repositories.SceneRepository
import com.soyle.stories.scene.repositories.getSceneOrError

class PinSymbolToSceneUseCase(
    private val sceneRepository: SceneRepository
) : PinSymbolToScene {
    override suspend fun invoke(sceneId: Scene.Id, symbolId: Symbol.Id, output: PinSymbolToScene.OutputPort) {
        val scene = sceneRepository.getSceneOrError(sceneId.uuid)
        val sceneUpdate = scene.withSymbolPinned(symbolId)
        if (sceneUpdate is Updated) {
            sceneRepository.updateScene(sceneUpdate.scene)
            output.symbolPinnedToScene(PinSymbolToScene.ResponseModel(sceneUpdate.event))
        }
    }
}