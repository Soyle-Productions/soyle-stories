package com.soyle.stories.scene.create

import com.soyle.stories.domain.scene.events.SceneCreated

interface SceneCreatedReceiver {
    suspend fun receiveSceneCreated(event: SceneCreated)
}