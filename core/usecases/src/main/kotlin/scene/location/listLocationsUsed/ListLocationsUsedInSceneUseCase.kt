package com.soyle.stories.usecase.scene.location.listLocationsUsed

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.location.listAllLocations.LocationItem
import com.soyle.stories.usecase.scene.SceneRepository

class ListLocationsUsedInSceneUseCase(
    private val sceneRepository: SceneRepository
) : ListLocationsUsedInScene {

    override suspend fun invoke(sceneId: Scene.Id, output: ListLocationsUsedInScene.OutputPort) {
        val scene = sceneRepository.getSceneOrError(sceneId.uuid)
        val response = ListLocationsUsedInScene.ResponseModel(scene.settings.map {
            LocationItem(it.id, it.locationName)
        })
        output.receiveLocationsUsedInScene(response)
    }

}