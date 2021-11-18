package com.soyle.stories.usecase.scene.deleteScene

import com.soyle.stories.domain.location.LocationUpdate
import com.soyle.stories.domain.location.Updated
import com.soyle.stories.domain.location.events.HostedSceneRemoved
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.SceneSettingLocation
import com.soyle.stories.domain.scene.events.SceneRemoved
import com.soyle.stories.domain.scene.order.SceneOrderUpdate
import com.soyle.stories.domain.scene.order.SuccessfulSceneOrderUpdate
import com.soyle.stories.usecase.location.LocationRepository
import com.soyle.stories.usecase.scene.SceneRepository

class DeleteSceneUseCase(
    private val sceneRepository: SceneRepository,
    private val locationRepository: LocationRepository
) : DeleteScene {

    override suspend fun invoke(sceneId: Scene.Id, output: DeleteScene.OutputPort) {
        val scene = sceneRepository.getSceneOrError(sceneId.uuid)

        val update = removeSceneFromSceneOrder(scene)
        val locationUpdates = removeSceneFromLocations(scene)

        commitChanges(scene, update, locationUpdates)

        output.receiveDeleteSceneResponse(response(update, locationUpdates))
    }

    private suspend fun removeSceneFromSceneOrder(scene: Scene): SceneOrderUpdate.Successful<SceneRemoved> {
        val sceneOrder = sceneRepository.getSceneIdsInOrder(scene.projectId)!!
        return sceneOrder.withScene(scene.id)!!.removed() as SuccessfulSceneOrderUpdate
    }

    private suspend fun removeSceneFromLocations(scene: Scene): List<LocationUpdate<HostedSceneRemoved>> {
        val locations = locationRepository.getLocationsById(scene.settings.map(SceneSettingLocation::id).toSet())
        val locationUpdates = locations.mapNotNull {
            it.withHostedScene(scene.id)?.removed()
        }
        return locationUpdates
    }

    private suspend fun commitChanges(
        scene: Scene,
        update: SceneOrderUpdate.Successful<SceneRemoved>,
        locationUpdates: List<LocationUpdate<HostedSceneRemoved>>
    ) {
        sceneRepository.removeScene(scene.id)
        sceneRepository.updateSceneOrder(update.sceneOrder)
        locationRepository.updateLocations(locationUpdates.map { it.location }.toSet())
    }

    private fun response(
        update: SceneOrderUpdate.Successful<SceneRemoved>,
        locationUpdates: List<LocationUpdate<HostedSceneRemoved>>
    ) = DeleteScene.ResponseModel(
        update.change,
        locationUpdates.filterIsInstance<Updated<HostedSceneRemoved>>().map { it.event }
    )
}
