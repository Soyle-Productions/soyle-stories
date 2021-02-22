package com.soyle.stories.scene.locationsInScene.linkLocationToScene

import com.soyle.stories.domain.scene.LocationUsedInScene

interface LocationUsedInSceneReceiver {

    suspend fun receiveLocationUsedInScene(locationUsedInScene: LocationUsedInScene)

}