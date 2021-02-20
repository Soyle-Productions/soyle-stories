package com.soyle.stories.usecase.scene.locationsInScene.removeLocationFromScene

import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.scene.LocationRemovedFromScene
import com.soyle.stories.domain.scene.Scene

interface RemoveLocationFromScene {

    suspend operator fun invoke(sceneId: Scene.Id, locationId: Location.Id, output: OutputPort)

    class ResponseModel(
        val locationRemovedFromScene: LocationRemovedFromScene
    )

    interface OutputPort {
        suspend fun locationRemovedFromScene(response: ResponseModel)
    }

}