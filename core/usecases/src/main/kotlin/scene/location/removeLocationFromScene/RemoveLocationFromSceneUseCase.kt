package com.soyle.stories.usecase.scene.location.removeLocationFromScene

import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.SceneUpdate.Successful
import com.soyle.stories.usecase.location.LocationRepository
import com.soyle.stories.usecase.scene.SceneDoesNotUseLocation
import com.soyle.stories.usecase.scene.SceneRepository

class RemoveLocationFromSceneUseCase(
    private val sceneRepository: SceneRepository,
    private val locationRepository: LocationRepository
) : RemoveLocationFromScene {

    override suspend fun invoke(
        sceneId: Scene.Id,
        locationId: Location.Id,
        output: RemoveLocationFromScene.OutputPort
    ) {
        val scene = sceneRepository.getSceneOrError(sceneId.uuid)
        val updatedScene = scene.withoutLocation(locationId)
        if (updatedScene is Successful) {
            val location = locationRepository.getLocationById(locationId)
            val locationUpdate = if (location != null) {
                location.withHostedScene(scene.id)!!.removed()
                        as com.soyle.stories.domain.location.Updated
            } else null
            sceneRepository.updateScene(updatedScene.scene)
            if (locationUpdate != null) locationRepository.updateLocation(locationUpdate.location)
            val response = RemoveLocationFromScene.ResponseModel(updatedScene.event, locationUpdate?.event)
            output.locationRemovedFromScene(response)
        } else throw SceneDoesNotUseLocation(scene.id, locationId)
    }

}
