package com.soyle.stories.location.deleteLocation

import com.soyle.stories.scene.locationsInScene.removeLocationFromScene.LocationRemovedFromSceneReceiver
import com.soyle.stories.usecase.location.deleteLocation.DeleteLocation

class DeleteLocationOutput(
    private val deletedLocationReceiver: DeletedLocationReceiver,
    private val locationRemovedFromSceneReceiver: LocationRemovedFromSceneReceiver
) : DeleteLocation.OutputPort {

    override suspend fun receiveDeleteLocationResponse(response: DeleteLocation.ResponseModel) {
        deletedLocationReceiver.receiveDeletedLocation(response.deletedLocation)
        response.locationRemovedFromScenes.forEach {
            locationRemovedFromSceneReceiver.receiveLocationRemovedFromScene(it)
        }
    }

}