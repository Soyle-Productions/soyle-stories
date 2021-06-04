package com.soyle.stories.scene.locationsInScene.linkLocationToScene

import com.soyle.stories.location.hostedScene.SceneHostedReceiver
import com.soyle.stories.usecase.scene.location.linkLocationToScene.LinkLocationToScene

class LinkLocationToSceneOutput(
    private val locationLinkedToSceneReceiver: LocationUsedInSceneReceiver,
    private val sceneHostedReceiver: SceneHostedReceiver
) : LinkLocationToScene.OutputPort {

    override suspend fun locationLinkedToScene(response: LinkLocationToScene.ResponseModel) {
        locationLinkedToSceneReceiver.receiveLocationUsedInScene(response.locationUsedInScene)
        sceneHostedReceiver.receiveSceneHostedAtLocation(response.sceneHostedAtLocation)
    }
}
