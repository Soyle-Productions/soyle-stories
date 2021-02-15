package com.soyle.stories.scene.sceneFrame

import com.soyle.stories.domain.scene.SceneFrameValueChanged

interface SceneFrameValueChangedReceiver {

    suspend fun receiveSceneFrameValueChanged(sceneFrameValueChanged: SceneFrameValueChanged)
}