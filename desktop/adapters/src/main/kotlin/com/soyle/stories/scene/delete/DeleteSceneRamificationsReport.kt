package com.soyle.stories.scene.delete

import com.soyle.stories.usecase.scene.getPotentialChangesFromDeletingScene.GetPotentialChangesFromDeletingScene

interface DeleteSceneRamificationsReport : GetPotentialChangesFromDeletingScene.OutputPort {
    suspend fun requestContinuation(): Unit?
}