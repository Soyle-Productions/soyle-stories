package com.soyle.stories.scene.target

import com.soyle.stories.common.Notifier

class SceneTargetedNotifier : Notifier<SceneTargetedReceiver>(), SceneTargetedReceiver {

    override suspend fun receiveSceneTargeted(event: SceneTargeted) {
        notifyAll { it.receiveSceneTargeted(event) }
    }
}