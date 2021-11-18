package com.soyle.stories.scene.delete

interface DeleteScenePrompt {
    suspend fun requestConfirmation(): Boolean?
    suspend fun requestShouldConfirmNextTime(): Boolean?
}