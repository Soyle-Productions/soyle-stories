package com.soyle.stories.scene.locationsInScene.removeLocationFromScene

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.scene.location.removeLocationFromScene.RemoveLocationFromScene
import kotlinx.coroutines.Job

class RemoveLocationFromSceneControllerImpl(
    private val threadTransformer: ThreadTransformer,
    private val removeLocationFromScene: RemoveLocationFromScene,
    private val removeLocationFromSceneOutput: RemoveLocationFromScene.OutputPort
) : RemoveLocationFromSceneController {

    override fun removeLocation(sceneId: Scene.Id, locationId: Location.Id): Job {
        return threadTransformer.async {
            removeLocationFromScene(sceneId, locationId, removeLocationFromSceneOutput)
        }
    }

}