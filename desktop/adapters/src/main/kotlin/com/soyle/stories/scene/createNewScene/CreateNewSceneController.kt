package com.soyle.stories.scene.createNewScene

import com.soyle.stories.domain.validation.NonBlankString

interface CreateNewSceneController {

	fun createNewScene(name: NonBlankString)

	fun createNewSceneBefore(name: NonBlankString, sceneId: String)
	fun createNewSceneAfter(name: NonBlankString, sceneId: String)

}