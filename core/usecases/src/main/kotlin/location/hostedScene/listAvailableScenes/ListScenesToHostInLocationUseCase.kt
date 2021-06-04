package com.soyle.stories.usecase.location.hostedScene.listAvailableScenes

import com.soyle.stories.domain.location.Location
import com.soyle.stories.usecase.location.LocationRepository
import com.soyle.stories.usecase.scene.SceneRepository

class ListScenesToHostInLocationUseCase(
    private val locationRepository: LocationRepository,
    private val sceneRepository: SceneRepository
) : ListScenesToHostInLocation {

    override suspend fun invoke(locationId: Location.Id, output: ListScenesToHostInLocation.OutputPort) {
        val location = locationRepository.getLocationOrError(locationId)
        val response = sceneRepository
            .listAllScenesInProject(
                projectId = location.projectId,
                exclude = location.hostedScenes.map { it.id }.toSet()
            )
            .map { ListScenesToHostInLocation.AvailableSceneToHost(it.id, it.name.value) }
            .let { ListScenesToHostInLocation.ResponseModel(location.id, it) }
        output.receiveScenesAvailableToHostInLocation(response)
    }
}
