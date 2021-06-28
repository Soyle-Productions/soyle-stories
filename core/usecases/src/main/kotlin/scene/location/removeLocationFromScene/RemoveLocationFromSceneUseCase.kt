package com.soyle.stories.usecase.scene.location.removeLocationFromScene

import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.Updated
import com.soyle.stories.usecase.scene.SceneDoesNotUseLocation
import com.soyle.stories.usecase.scene.SceneRepository

class RemoveLocationFromSceneUseCase(
    private val sceneRepository: SceneRepository
) : RemoveLocationFromScene {
    override suspend fun invoke(
        sceneId: Scene.Id,
        locationId: Location.Id,
        output: RemoveLocationFromScene.OutputPort
    ) {
        val scene = sceneRepository.getSceneOrError(sceneId.uuid)
        val updatedScene = scene.withoutLocation(locationId)
        if (updatedScene is Updated) {
            sceneRepository.updateScene(updatedScene.scene)
            val response = RemoveLocationFromScene.ResponseModel(updatedScene.event)
            output.locationRemovedFromScene(response)
        } else throw SceneDoesNotUseLocation(scene.id, locationId)
    }

}