package com.soyle.stories.scene.target

interface SceneTargetedReceiver {
    suspend fun receiveSceneTargeted(event: SceneTargeted)
}