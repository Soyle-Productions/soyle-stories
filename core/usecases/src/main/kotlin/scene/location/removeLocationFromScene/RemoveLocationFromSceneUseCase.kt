package com.soyle.stories.usecase.scene.location.removeLocationFromScene

import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.Updated
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
        if (updatedScene is Updated) {
            val locationUpdate = locationRepository.getLocationOrError(locationId).withHostedScene(scene.id)!!.removed()
                    as com.soyle.stories.domain.location.Updated
            sceneRepository.updateScene(updatedScene.scene)
            locationRepository.updateLocation(locationUpdate.location)
            val response = RemoveLocationFromScene.ResponseModel(updatedScene.event, locationUpdate.event)
            output.locationRemovedFromScene(response)
        } else throw SceneDoesNotUseLocation(scene.id, locationId)
    }

}
