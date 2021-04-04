package com.soyle.stories.usecase.scene.locationsInScene.listLocationsToUse

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.location.listAllLocations.LocationItem

interface ListAvailableLocationsToUseInScene {

    suspend operator fun invoke(sceneId: Scene.Id, output: OutputPort)

    class ResponseModel(items: List<LocationItem>) : List<LocationItem> by items


    interface OutputPort {
        suspend fun receiveAvailableLocationsToUseInScene(response: ResponseModel)
    }

}