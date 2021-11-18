package com.soyle.stories.scene.create

import com.soyle.stories.common.Notifier
import com.soyle.stories.domain.scene.events.SceneCreated

class SceneCreatedNotifier : Notifier<SceneCreatedReceiver>(), SceneCreatedReceiver {
    override suspend fun receiveSceneCreated(event: SceneCreated) {
        notifyAll { it.receiveSceneCreated(event) }
    }
}