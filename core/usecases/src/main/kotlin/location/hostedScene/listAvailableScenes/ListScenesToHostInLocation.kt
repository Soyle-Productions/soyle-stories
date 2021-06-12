package com.soyle.stories.usecase.location.hostedScene.listAvailableScenes

import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.scene.Scene

interface ListScenesToHostInLocation {

    suspend operator fun invoke(locationId: Location.Id, output: OutputPort)

    class ResponseModel(val locationId: Location.Id, val availableScenesToHost: List<AvailableSceneToHost>)
    class AvailableSceneToHost(val sceneId: Scene.Id, val sceneName: String)

    fun interface OutputPort {
        suspend fun receiveScenesAvailableToHostInLocation(response: ResponseModel)
    }
}
