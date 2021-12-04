package com.soyle.stories.scene.create

import com.soyle.stories.domain.scene.events.SceneCreated
import com.soyle.stories.domain.scene.order.SceneOrderUpdate

interface SceneCreatedReceiver {
    suspend fun receiveSceneCreated(event: SceneCreated, orderUpdate: SceneOrderUpdate<*>)
}