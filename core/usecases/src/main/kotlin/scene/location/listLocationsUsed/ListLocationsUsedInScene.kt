package com.soyle.stories.usecase.scene.location.listLocationsUsed

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.location.listAllLocations.LocationItem

interface ListLocationsUsedInScene {

    suspend operator fun invoke(sceneId: Scene.Id, output: OutputPort)

    class ResponseModel(items: List<LocationItem>) : List<LocationItem> by items

    interface OutputPort {
        suspend fun receiveLocationsUsedInScene(response: ResponseModel)
    }

}