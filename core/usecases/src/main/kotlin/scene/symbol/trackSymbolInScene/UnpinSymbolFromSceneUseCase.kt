package com.soyle.stories.usecase.scene.symbol.trackSymbolInScene

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.events.SymbolUnpinnedFromScene
import com.soyle.stories.domain.scene.events.TrackedSymbolRemoved
import com.soyle.stories.domain.scene.SceneUpdate.Successful
import com.soyle.stories.domain.theme.Symbol
import com.soyle.stories.usecase.prose.ProseRepository
import com.soyle.stories.usecase.scene.SceneRepository

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

        if (sceneUpdate is Successful) {
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