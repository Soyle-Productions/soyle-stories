package com.soyle.stories.scene.target

fun interface SceneTargetedReceiver {
    suspend fun receiveSceneTargeted(event: SceneTargeted)
}