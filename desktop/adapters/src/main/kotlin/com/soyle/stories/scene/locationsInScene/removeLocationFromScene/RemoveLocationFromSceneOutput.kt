package com.soyle.stories.scene.locationsInScene.removeLocationFromScene

import com.soyle.stories.usecase.scene.location.removeLocationFromScene.RemoveLocationFromScene

class RemoveLocationFromSceneOutput(
    private val locationRemovedFromSceneReceiver: LocationRemovedFromSceneReceiver
) : RemoveLocationFromScene.OutputPort {

    override suspend fun locationRemovedFromScene(response: RemoveLocationFromScene.ResponseModel) {
        locationRemovedFromSceneReceiver.receiveLocationRemovedFromScene(response.locationRemovedFromScene)
    }

}