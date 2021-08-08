package com.soyle.stories.scene.locationsInScene.replace

import com.soyle.stories.location.hostedScene.HostedSceneRemovedReceiver
import com.soyle.stories.location.hostedScene.SceneHostedNotifier
import com.soyle.stories.scene.locationsInScene.linkLocationToScene.LocationUsedInSceneReceiver
import com.soyle.stories.scene.locationsInScene.removeLocationFromScene.LocationRemovedFromSceneReceiver
import com.soyle.stories.usecase.scene.location.replace.ReplaceSettingInScene

class ReplaceSettingInSceneOutput(
    private val locationRemovedFromSceneReceiver: LocationRemovedFromSceneReceiver,
    private val locationUsedInSceneReceiver: LocationUsedInSceneReceiver,
    private val sceneHostedNotifier: SceneHostedNotifier,
    private val hostedSceneRemovedReceiver: HostedSceneRemovedReceiver
) : ReplaceSettingInScene.OutputPort {

    override suspend fun replaceSettingInSceneResponse(response: ReplaceSettingInScene.ResponseModel) {
        locationRemovedFromSceneReceiver.receiveLocationRemovedFromScene(response.locationRemovedFromScene)
        response.locationRemovedFromScene.replacedBy?.let { locationUsedInSceneReceiver.receiveLocationUsedInScene(it) }
        sceneHostedNotifier.receiveSceneHostedAtLocation(response.sceneHostedAtLocation)
        response.hostedSceneRemoved?.let { hostedSceneRemovedReceiver.receiveHostedSceneRemoved(it) }
    }
}