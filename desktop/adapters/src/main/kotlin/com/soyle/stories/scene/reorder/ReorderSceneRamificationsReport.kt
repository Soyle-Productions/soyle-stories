package com.soyle.stories.scene.reorder

import com.soyle.stories.usecase.scene.getPotentialChangeFromReorderingScene.GetPotentialChangesFromReorderingScene

interface ReorderSceneRamificationsReport : GetPotentialChangesFromReorderingScene.OutputPort {
    suspend fun requestContinuation(): Unit?
}