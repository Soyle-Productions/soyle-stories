package com.soyle.stories.usecase.scene.location.listLocationsToUse

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.location.listAllLocations.LocationItem

interface ListAvailableLocationsToUseInScene {

    suspend operator fun invoke(sceneId: Scene.Id, output: OutputPort)

    class ResponseModel(items: List<LocationItem>) : List<LocationItem> by items


    fun interface OutputPort {
        suspend fun receiveAvailableLocationsToUseInScene(response: ResponseModel)
    }

}