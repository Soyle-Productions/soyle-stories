package com.soyle.stories.usecase.scene.location.replace

import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.location.events.HostedSceneRemoved
import com.soyle.stories.domain.location.events.SceneHostedAtLocation
import com.soyle.stories.domain.location.Updated as UpdatedLocation
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.SceneUpdate.UnSuccessful
import com.soyle.stories.domain.scene.SuccessfulSceneUpdate
import com.soyle.stories.usecase.location.LocationRepository
import com.soyle.stories.usecase.scene.SceneDoesNotUseLocation
import com.soyle.stories.usecase.scene.SceneRepository

class ReplaceSettingInSceneUseCase(
    private val sceneRepository: SceneRepository,
    private val locationRepository: LocationRepository
) : ReplaceSettingInScene {

    override suspend fun invoke(request: ReplaceSettingInScene.RequestModel, output: ReplaceSettingInScene.OutputPort) {
        val scene = sceneRepository.getSceneOrError(request.sceneId.uuid)
        val response = scene.replace(request.locationId, request.replacementLocationId) ?: return
        output.replaceSettingInSceneResponse(response)
    }

    private suspend fun Scene.replace(locationId: Location.Id, replacementId: Location.Id): ReplaceSettingInScene.ResponseModel?
    {
        val sceneSettingOps = withSetting(locationId) ?: throw SceneDoesNotUseLocation(id, locationId)
        val replacementLocation = locationRepository.getLocationOrError(replacementId)

        val sceneUpdate = sceneSettingOps.replacedWith(replacementLocation)

        if (sceneUpdate !is SuccessfulSceneUpdate) {
            sceneUpdate as UnSuccessful
            sceneUpdate.reason?.let { throw it }
            return null
        }

        sceneRepository.updateScene(sceneUpdate.scene)

        return ReplaceSettingInScene.ResponseModel(
            sceneUpdate.event,
            replacementLocation.hostScene(this),
            locationRepository.getLocationById(locationId)?.removeHostedScene(this)
        )
    }

    private suspend fun Location.hostScene(scene: Scene): SceneHostedAtLocation
    {
        val update = withSceneHosted(scene.id, scene.name.value)
        locationRepository.updateLocation(update.location)
        return (update as UpdatedLocation).event
    }

    private suspend fun Location.removeHostedScene(scene: Scene): HostedSceneRemoved?
    {
        val update = withHostedScene(scene.id)?.removed()
        if (update is UpdatedLocation) {
            locationRepository.updateLocation(update.location)
            return update.event
        }
        return null
    }
}