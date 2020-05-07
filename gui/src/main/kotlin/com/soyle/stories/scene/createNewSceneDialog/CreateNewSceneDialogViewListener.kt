package com.soyle.stories.scene.createNewSceneDialog

interface CreateNewSceneDialogViewListener {

	fun getValidState()
	fun createScene(name: String)

}