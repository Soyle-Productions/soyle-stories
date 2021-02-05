package com.soyle.stories.scene.usecases.trackSymbolInScene

import com.soyle.stories.entities.Scene
import com.soyle.stories.entities.SymbolUnpinnedFromScene
import com.soyle.stories.entities.TrackedSymbolRemoved
import com.soyle.stories.entities.Updated
import com.soyle.stories.entities.theme.Symbol
import com.soyle.stories.prose.repositories.ProseRepository
import com.soyle.stories.prose.repositories.getProseOrError
import com.soyle.stories.scene.repositories.SceneRepository
import com.soyle.stories.scene.repositories.getSceneOrError

class UnpinSymbolFromSceneUseCase(
    private val sceneRepository: SceneRepository,
    private val proseRepository: ProseRepository
) : UnpinSymbolFromScene {
    override suspend fun invoke(sceneId: Scene.Id, symbolId: Symbol.Id, output: UnpinSymbolFromScene.OutputPort) {
        val scene = sceneRepository.getSceneOrError(sceneId.uuid)
        val prose = proseRepository.getProseOrError(scene.proseId)

        val sceneUpdate = if (prose.mentions.any { it.entityId.id == symbolId })
            scene.withSymbolUnpinned(symbolId)
        else
            scene.withoutSymbolTracked(symbolId)

        if (sceneUpdate is Updated) {
            sceneRepository.updateScene(sceneUpdate.scene)
            output.symbolUnpinnedFromScene(
                UnpinSymbolFromScene.ResponseModel(
                    sceneUpdate.event as? SymbolUnpinnedFromScene,
                    sceneUpdate.event as? TrackedSymbolRemoved
                )
            )
        }
    }
}