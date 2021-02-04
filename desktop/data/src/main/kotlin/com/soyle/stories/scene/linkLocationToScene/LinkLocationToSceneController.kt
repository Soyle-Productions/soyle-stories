package com.soyle.stories.scene.linkLocationToScene

interface LinkLocationToSceneController {

	fun linkLocationToScene(sceneId: String, locationId: String)
	fun clearLocationFromScene(sceneId: String)

}