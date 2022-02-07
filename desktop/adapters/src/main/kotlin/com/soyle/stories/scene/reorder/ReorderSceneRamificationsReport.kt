package com.soyle.stories.scene.reorder

import com.soyle.stories.usecase.scene.reorderScene.GetPotentialChangesFromReorderingScene

interface ReorderSceneRamificationsReport : GetPotentialChangesFromReorderingScene.OutputPort {
    suspend fun requestContinuation(): Unit?
}