package com.soyle.stories.scene.createNewSceneDialog

import com.soyle.stories.common.NonBlankString

interface CreateNewSceneDialogViewListener {

	fun getValidState()
	fun createScene(name: NonBlankString)
	fun createSceneBefore(name: NonBlankString, relativeScene: String)
	fun createSceneAfter(name: NonBlankString, relativeScene: String)

}