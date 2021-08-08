package com.soyle.stories.usecase.scene.location.removeLocationFromScene

import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.location.events.HostedSceneRemoved
import com.soyle.stories.domain.scene.events.LocationRemovedFromScene
import com.soyle.stories.domain.scene.Scene

interface RemoveLocationFromScene {

    suspend operator fun invoke(sceneId: Scene.Id, locationId: Location.Id, output: OutputPort)

    class ResponseModel(
        val locationRemovedFromScene: LocationRemovedFromScene,
        val hostedSceneRemoved: HostedSceneRemoved?
    )

    interface OutputPort {
        suspend fun locationRemovedFromScene(response: ResponseModel)
    }

}