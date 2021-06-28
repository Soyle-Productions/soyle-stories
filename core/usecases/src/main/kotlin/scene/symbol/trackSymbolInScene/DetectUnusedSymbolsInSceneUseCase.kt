package com.soyle.stories.usecase.scene.symbol.trackSymbolInScene

import com.soyle.stories.domain.prose.mentioned
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.prose.ProseRepository
import com.soyle.stories.usecase.scene.SceneRepository

class DetectUnusedSymbolsInSceneUseCase(
    private val sceneRepository: SceneRepository,
    private val proseRepository: ProseRepository
) : DetectUnusedSymbolsInScene {

    override suspend fun invoke(sceneId: Scene.Id, output: DetectUnusedSymbolsInScene.OutputPort) {
        val scene = sceneRepository.getSceneOrError(sceneId.uuid)
        val prose = proseRepository.getProseOrError(scene.proseId)

        output.receiveDetectedUnusedSymbols(
            DetectUnusedSymbolsInScene.ResponseModel(
                scene.id,
                scene.trackedSymbols
                    .asSequence()
                    .filterNot { prose.containsMentionOf(it.symbolId.mentioned(it.themeId)) }
                    .map { it.symbolId }
                    .toSet()
            )
        )
    }
}