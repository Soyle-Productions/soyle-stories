package com.soyle.stories.scene.create

import com.soyle.stories.common.Notifier
import com.soyle.stories.domain.scene.events.SceneCreated
import com.soyle.stories.domain.scene.order.SceneOrderUpdate

class SceneCreatedNotifier : Notifier<SceneCreatedReceiver>(), SceneCreatedReceiver {
    override suspend fun receiveSceneCreated(event: SceneCreated, orderUpdate: SceneOrderUpdate<*>) {
        notifyAll { it.receiveSceneCreated(event, orderUpdate) }
    }
}