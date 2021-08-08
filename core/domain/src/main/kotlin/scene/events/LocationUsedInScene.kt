package com.soyle.stories.domain.scene.events

import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.SceneSettingLocation

data class LocationUsedInScene(override val sceneId: Scene.Id, val locationId: Location.Id, val locationName: String) : SceneEvent() {
    constructor(sceneId: Scene.Id, sceneSetting: SceneSettingLocation) : this(sceneId, sceneSetting.id, sceneSetting.locationName)
}