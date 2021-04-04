package com.soyle.stories.domain.scene.events

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.SceneSettingLocation

data class LocationRemovedFromScene(override val sceneId: Scene.Id, val sceneSetting: SceneSettingLocation) :
    SceneEvent()