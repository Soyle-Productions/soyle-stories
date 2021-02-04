package com.soyle.stories.scene.usecases.trackSymbolInScene

import com.soyle.stories.entities.Scene
import com.soyle.stories.prose.repositories.ProseRepository
import com.soyle.stories.prose.repositories.getProseOrError
import com.soyle.stories.scene.repositories.SceneRepository
import com.soyle.stories.scene.repositories.getSceneOrError

class DetectUnusedSymbolsInSceneUseCase(
    private val sceneRepository: SceneRepository,
    private val proseRepository: ProseRepository
) : DetectUnusedSymbolsInScene {
    override suspend fun invoke(sceneId: Scene.Id, output: DetectUnusedSymbolsInScene.OutputPort) {
        val scene = sceneRepository.getSceneOrError(sceneId.uuid)
        val prose = proseRepository.getProseOrError(scene.proseId)

        output.receiveDetectedUnusedSymbols(DetectUnusedSymbolsInScene.ResponseModel(
            scene.id,
            scene.trackedSymbols
            .asSequence()
            .filterNot { prose.containsMentionOf(it.symbolId) }
            .map { it.symbolId }
            .toSet()))
    }
}