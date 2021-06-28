package com.soyle.stories.usecase.scene.sceneFrame

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.scene.SceneRepository

class GetSceneFrameUseCase(private val sceneRepository: SceneRepository) : GetSceneFrame {
    override suspend fun invoke(sceneId: Scene.Id, output: GetSceneFrame.OutputPort) {
        val scene = sceneRepository.getSceneOrError(sceneId.uuid)
        val response = GetSceneFrame.ResponseModel(
            sceneId,
            scene.conflict.value,
            scene.resolution.value
        )
        output.receiveSceneFrame(response)
    }
}