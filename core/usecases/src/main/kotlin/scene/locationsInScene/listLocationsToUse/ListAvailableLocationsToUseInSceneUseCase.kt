package com.soyle.stories.usecase.scene.locationsInScene.listLocationsToUse

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.location.LocationRepository
import com.soyle.stories.usecase.location.listAllLocations.LocationItem
import com.soyle.stories.usecase.scene.SceneRepository

class ListAvailableLocationsToUseInSceneUseCase(
    private val sceneRepository: SceneRepository,
    private val locationRepository: LocationRepository
) : ListAvailableLocationsToUseInScene {

    override suspend fun invoke(sceneId: Scene.Id, output: ListAvailableLocationsToUseInScene.OutputPort) {
        val scene = sceneRepository.getSceneOrError(sceneId.uuid)
        val locations = locationRepository.getAllLocationsInProject(scene.projectId)
        val response = ListAvailableLocationsToUseInScene.ResponseModel(
            locations.asSequence()
                .filterNot { scene.settings.containsEntityWithId(it.id) }
                .map { LocationItem(it.id, it.name.value) }
                .toList()
        )
        output.receiveAvailableLocationsToUseInScene(response)
    }
}