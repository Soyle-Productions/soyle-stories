package com.soyle.stories.domain.scene.events

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.SceneEvent
import com.soyle.stories.domain.scene.SceneSettingLocation

data class SceneSettingLocationRenamed(override val sceneId: Scene.Id, val sceneSettingLocation: SceneSettingLocation) : SceneEvent()