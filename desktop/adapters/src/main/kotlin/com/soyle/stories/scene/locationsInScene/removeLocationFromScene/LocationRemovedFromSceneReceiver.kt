package com.soyle.stories.scene.locationsInScene.removeLocationFromScene

import com.soyle.stories.domain.scene.LocationRemovedFromScene

interface LocationRemovedFromSceneReceiver {
    suspend fun receiveLocationRemovedFromScene(locationRemovedFromScene: LocationRemovedFromScene)
}