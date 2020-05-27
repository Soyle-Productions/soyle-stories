package com.soyle.stories.scene.createNewScene

interface CreateNewSceneController {

	fun createNewScene(name: String)

	fun createNewSceneBefore(name: String, sceneId: String)
	fun createNewSceneAfter(name: String, sceneId: String)

}