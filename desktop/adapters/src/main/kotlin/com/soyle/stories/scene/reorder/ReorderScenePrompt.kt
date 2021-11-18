package com.soyle.stories.scene.reorder

interface ReorderScenePrompt {
    suspend fun requestConfirmation(): Boolean?
    suspend fun requestShouldShowNextTime(): Boolean?
}