package com.soyle.stories.scene.delete

import com.soyle.stories.usecase.scene.delete.GetPotentialChangesFromDeletingScene

interface DeleteSceneRamificationsReport : GetPotentialChangesFromDeletingScene.OutputPort {
    suspend fun requestContinuation(): Unit?
}