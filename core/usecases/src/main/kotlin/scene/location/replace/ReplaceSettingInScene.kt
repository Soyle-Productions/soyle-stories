package com.soyle.stories.usecase.scene.location.replace

import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.location.events.HostedSceneRemoved
import com.soyle.stories.domain.location.events.SceneHostedAtLocation
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.events.LocationRemovedFromScene

interface ReplaceSettingInScene {

    class RequestModel(
        val sceneId: Scene.Id,
        val locationId: Location.Id,
        val replacementLocationId: Location.Id
    )

    suspend operator fun invoke(request: RequestModel, output: OutputPort)

    class ResponseModel(
        val locationRemovedFromScene: LocationRemovedFromScene,
        val sceneHostedAtLocation: SceneHostedAtLocation,
        val hostedSceneRemoved: HostedSceneRemoved?
    )

    interface OutputPort {
        suspend fun replaceSettingInSceneResponse(response: ResponseModel)
    }

}