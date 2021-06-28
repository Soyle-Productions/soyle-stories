package com.soyle.stories.scene.sceneFrame

import com.soyle.stories.common.Notifier
import com.soyle.stories.domain.scene.events.SceneFrameValueChanged

class SceneFrameValueChangedNotifier : Notifier<SceneFrameValueChangedReceiver>(), SceneFrameValueChangedReceiver {
    override suspend fun receiveSceneFrameValueChanged(sceneFrameValueChanged: SceneFrameValueChanged) {
        notifyAll { it.receiveSceneFrameValueChanged(sceneFrameValueChanged) }
    }
}