package com.soyle.stories.usecase.scene.sceneFrame

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.SceneFrameValue
import com.soyle.stories.domain.scene.Updated
import com.soyle.stories.usecase.scene.SceneRepository

class SetSceneFrameValueUseCase(
    private val sceneRepository: SceneRepository
) : SetSceneFrameValue {
    override suspend fun invoke(sceneId: Scene.Id, value: SceneFrameValue, output: SetSceneFrameValue.OutputPort) {
        val scene = sceneRepository.getSceneOrError(sceneId.uuid)
        val update = scene.withSceneFrameValue(value)
        if (update is Updated) {
            sceneRepository.updateScene(update.scene)
            output.sceneFrameValueSet(SetSceneFrameValue.ResponseModel(update.event))
        } else {
            output.sceneFrameValueSet(SetSceneFrameValue.ResponseModel(null))
        }
    }
}