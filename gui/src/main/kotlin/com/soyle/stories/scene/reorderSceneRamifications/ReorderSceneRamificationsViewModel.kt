package com.soyle.stories.scene.reorderSceneRamifications

import com.soyle.stories.scene.deleteSceneRamifications.SceneRamificationsViewModel

data class ReorderSceneRamificationsViewModel(
    val invalid: Boolean = true,
    val okMessage: String,
    val scenes: List<SceneRamificationsViewModel>
)