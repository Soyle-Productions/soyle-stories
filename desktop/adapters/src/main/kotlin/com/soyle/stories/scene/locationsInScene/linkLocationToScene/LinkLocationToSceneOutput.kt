package com.soyle.stories.scene.locationsInScene.linkLocationToScene

import com.soyle.stories.usecase.scene.location.linkLocationToScene.LinkLocationToScene

class LinkLocationToSceneOutput(
    private val locationLinkedToSceneReceiver: LocationUsedInSceneReceiver
) : LinkLocationToScene.OutputPort {

    override suspend fun locationLinkedToScene(response: LinkLocationToScene.ResponseModel) {
        locationLinkedToSceneReceiver.receiveLocationUsedInScene(response.locationUsedInScene)
    }

}