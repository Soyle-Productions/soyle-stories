package com.soyle.stories.usecase.scene.sceneFrame

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.SceneFrameValue
import com.soyle.stories.domain.scene.events.SceneFrameValueChanged

interface SetSceneFrameValue {

    suspend operator fun invoke(sceneId: Scene.Id, value: SceneFrameValue, output: OutputPort)

    class ResponseModel(
        val sceneFrameValueChanged: SceneFrameValueChanged?
    )

    interface OutputPort {
        suspend fun sceneFrameValueSet(response: ResponseModel)
    }

}