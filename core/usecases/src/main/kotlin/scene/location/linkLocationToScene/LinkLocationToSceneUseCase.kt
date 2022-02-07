package com.soyle.stories.usecase.scene.location.linkLocationToScene

import com.soyle.stories.domain.location.Updated as LocationUpdated
import com.soyle.stories.domain.scene.SceneUpdate.Successful
import com.soyle.stories.usecase.location.LocationRepository
import com.soyle.stories.usecase.scene.SceneRepository
import com.soyle.stories.usecase.scene.location.linkLocationToScene.LinkLocationToScene.*

class LinkLocationToSceneUseCase(
    private val sceneRepository: SceneRepository,
    private val locationRepository: LocationRepository
) : LinkLocationToScene {

    override suspend fun invoke(request: RequestModel, output: OutputPort) {
        val scene = sceneRepository.getSceneOrError(request.sceneId.uuid)
        val location = locationRepository.getLocationOrError(request.locationId)
        val sceneUpdate = scene.withLocationLinked(location)
        if (sceneUpdate is Successful) {
            val (updatedLocation, sceneHostedAtLocation) =
                location.withSceneHosted(scene.id, scene.name.value) as LocationUpdated
            val response = ResponseModel(sceneUpdate.event, sceneHostedAtLocation)
            sceneRepository.updateScene(sceneUpdate.scene)
            locationRepository.updateLocation(updatedLocation)
            output.locationLinkedToScene(response)
        }
    }

}
