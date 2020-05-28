package com.soyle.stories.scene.createNewSceneDialog

interface CreateNewSceneDialogViewListener {

	fun getValidState()
	fun createScene(name: String)
	fun createSceneBefore(name: String, relativeScene: String)
	fun createSceneAfter(name: String, relativeScene: String)

}