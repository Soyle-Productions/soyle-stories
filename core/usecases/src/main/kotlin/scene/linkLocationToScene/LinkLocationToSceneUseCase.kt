package com.soyle.stories.usecase.scene.linkLocationToScene

import com.soyle.stories.domain.scene.Updated
import com.soyle.stories.usecase.location.LocationRepository
import com.soyle.stories.usecase.scene.SceneRepository
import com.soyle.stories.usecase.scene.linkLocationToScene.LinkLocationToScene.*

class LinkLocationToSceneUseCase(
    private val sceneRepository: SceneRepository,
    private val locationRepository: LocationRepository
) : LinkLocationToScene {
    override suspend fun invoke(request: RequestModel, output: OutputPort) {
        val scene = sceneRepository.getSceneOrError(request.sceneId.uuid)
        val location = locationRepository.getLocationOrError(request.locationId)
        val sceneUpdate = scene.withLocationLinked(location)
        if (sceneUpdate is Updated) {
            val response = ResponseModel(sceneUpdate.event)
            sceneRepository.updateScene(sceneUpdate.scene)
            output.locationLinkedToScene(response)
        }
    }

}