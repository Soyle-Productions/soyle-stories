package com.soyle.stories.scene.locationsInScene.linkLocationToScene

import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.scene.Scene

interface LinkLocationToSceneController {

	fun linkLocationToScene(sceneId: Scene.Id, locationId: Location.Id)

}