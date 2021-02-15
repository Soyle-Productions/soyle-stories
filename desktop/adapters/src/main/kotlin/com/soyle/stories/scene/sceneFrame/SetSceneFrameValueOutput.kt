package com.soyle.stories.scene.sceneFrame

import com.soyle.stories.usecase.scene.sceneFrame.SetSceneFrameValue

class SetSceneFrameValueOutput(
    private val sceneFrameValueChangedReceiver: SceneFrameValueChangedReceiver
) : SetSceneFrameValue.OutputPort {
    override suspend fun sceneFrameValueSet(response: SetSceneFrameValue.ResponseModel) {
        response.sceneFrameValueChanged?.let {
            sceneFrameValueChangedReceiver.receiveSceneFrameValueChanged(it)
        }
    }
}