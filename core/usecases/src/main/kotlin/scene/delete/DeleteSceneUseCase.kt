package com.soyle.stories.usecase.scene.delete

import com.soyle.stories.domain.location.LocationUpdate
import com.soyle.stories.domain.location.Updated
import com.soyle.stories.domain.location.events.HostedSceneRemoved
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.SceneSettingLocation
import com.soyle.stories.domain.scene.events.SceneRemoved
import com.soyle.stories.domain.scene.order.SceneOrderUpdate
import com.soyle.stories.domain.scene.order.SuccessfulSceneOrderUpdate
import com.soyle.stories.domain.storyevent.SuccessfulStoryEventUpdate
import com.soyle.stories.domain.storyevent.events.StoryEventUncoveredFromScene
import com.soyle.stories.usecase.location.LocationRepository
import com.soyle.stories.usecase.scene.SceneRepository
import com.soyle.stories.usecase.storyevent.StoryEventRepository

class DeleteSceneUseCase(
    private val sceneRepository: SceneRepository,
    private val locationRepository: LocationRepository,
    private val storyEventRepository: StoryEventRepository
) : DeleteScene {

    override suspend fun invoke(sceneId: Scene.Id, output: DeleteScene.OutputPort) {
        val scene = sceneRepository.getSceneOrError(sceneId.uuid)

        val update = removeSceneFromSceneOrder(scene)
        val locationUpdates = removeSceneFromLocations(scene)
        val storyEventUpdates = uncoverStoryEvents(scene)

        commitChanges(update, locationUpdates, storyEventUpdates)

        output.receiveDeleteSceneResponse(response(update, locationUpdates, storyEventUpdates))
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

    private suspend fun uncoverStoryEvents(scene: Scene) =
        storyEventRepository.getStoryEventsCoveredByScene(scene.id)
            .mapNotNull { it.withoutCoverage() as? SuccessfulStoryEventUpdate }

    private suspend fun commitChanges(
        update: SceneOrderUpdate.Successful<SceneRemoved>,
        locationUpdates: List<LocationUpdate<HostedSceneRemoved>>,
        storyEventUpdates: List<SuccessfulStoryEventUpdate<StoryEventUncoveredFromScene>>
    ) {
        sceneRepository.removeScene(update.change.sceneId)
        sceneRepository.updateSceneOrder(update.sceneOrder)
        locationRepository.updateLocations(locationUpdates.map { it.location }.toSet())
        storyEventUpdates.forEach { storyEventRepository.updateStoryEvent(it.storyEvent) }
    }

    private fun response(
        update: SceneOrderUpdate.Successful<SceneRemoved>,
        locationUpdates: List<LocationUpdate<HostedSceneRemoved>>,
        storyEventUpdates: List<SuccessfulStoryEventUpdate<StoryEventUncoveredFromScene>>
    ) = DeleteScene.ResponseModel(
        update.change,
        storyEventUpdates.map { it.change },
        locationUpdates.filterIsInstance<Updated<HostedSceneRemoved>>().map { it.event },
    )
}
