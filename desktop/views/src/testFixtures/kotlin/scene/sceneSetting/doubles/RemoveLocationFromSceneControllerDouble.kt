package com.soyle.stories.desktop.view.scene.sceneSetting.doubles

import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.scene.locationsInScene.removeLocationFromScene.RemoveLocationFromSceneController
import kotlinx.coroutines.Job

class RemoveLocationFromSceneControllerDouble(
    private val onRemoveLocation: (Scene.Id, Location.Id) -> Unit = { _, _ -> },
    private val job: Job = Job()
) : RemoveLocationFromSceneController {

    override fun removeLocation(sceneId: Scene.Id, locationId: Location.Id): Job {
        onRemoveLocation(sceneId, locationId)
        return job
    }
}