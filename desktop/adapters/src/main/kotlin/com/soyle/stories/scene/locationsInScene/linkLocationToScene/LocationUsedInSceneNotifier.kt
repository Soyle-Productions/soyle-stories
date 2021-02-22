package com.soyle.stories.scene.locationsInScene.linkLocationToScene

import com.soyle.stories.common.Notifier
import com.soyle.stories.domain.scene.LocationUsedInScene

class LocationUsedInSceneNotifier : Notifier<LocationUsedInSceneReceiver>(), LocationUsedInSceneReceiver {
    override suspend fun receiveLocationUsedInScene(locationUsedInScene: LocationUsedInScene) {
        notifyAll { it.receiveLocationUsedInScene(locationUsedInScene) }
    }
}