package com.soyle.stories.scene.locationsInScene.removeLocationFromScene

import com.soyle.stories.common.Notifier
import com.soyle.stories.domain.scene.LocationRemovedFromScene

class LocationRemovedFromSceneNotifier : Notifier<LocationRemovedFromSceneReceiver>(), LocationRemovedFromSceneReceiver {
    override suspend fun receiveLocationRemovedFromScene(locationRemovedFromScene: LocationRemovedFromScene) {
        notifyAll { it.receiveLocationRemovedFromScene(locationRemovedFromScene) }
    }
}