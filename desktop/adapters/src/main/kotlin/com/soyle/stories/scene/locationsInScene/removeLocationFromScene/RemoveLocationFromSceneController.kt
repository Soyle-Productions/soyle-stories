package com.soyle.stories.scene.locationsInScene.removeLocationFromScene

import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.scene.Scene
import kotlinx.coroutines.Job

interface RemoveLocationFromSceneController {
    fun removeLocation(sceneId: Scene.Id, locationId: Location.Id): Job
}