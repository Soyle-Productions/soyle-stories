package com.soyle.stories.scene.locationsInScene.removeLocationFromScene

import com.soyle.stories.domain.location.events.HostedSceneRemoved
import com.soyle.stories.location.hostedScene.HostedSceneRemovedReceiver
import com.soyle.stories.usecase.scene.location.removeLocationFromScene.RemoveLocationFromScene

class RemoveLocationFromSceneOutput(
    private val locationRemovedFromSceneReceiver: LocationRemovedFromSceneReceiver,
    private val hostedSceneRemovedReceiver: HostedSceneRemovedReceiver
) : RemoveLocationFromScene.OutputPort {

    override suspend fun locationRemovedFromScene(response: RemoveLocationFromScene.ResponseModel) {
        locationRemovedFromSceneReceiver.receiveLocationRemovedFromScene(response.locationRemovedFromScene)
        response.hostedSceneRemoved?.let { hostedSceneRemovedReceiver.receiveHostedSceneRemoved(it) }
    }

}
