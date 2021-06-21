package com.soyle.stories.scene.setting

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.location.items.LocationItemViewModel

data class SceneSettingViewModel(
    val targetSceneId: Scene.Id?,
    val usedLocations: List<LocationItemViewModel>,
    val availableLocations: List<LocationItemViewModel>?
)