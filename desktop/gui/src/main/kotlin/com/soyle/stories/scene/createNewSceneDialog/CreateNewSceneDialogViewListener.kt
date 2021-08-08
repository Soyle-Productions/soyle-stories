package com.soyle.stories.scene.createNewSceneDialog

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.validation.NonBlankString
import kotlinx.coroutines.Deferred

interface CreateNewSceneDialogViewListener {

	fun getValidState()
	fun createScene(name: NonBlankString): Deferred<Scene.Id>
	fun createSceneBefore(name: NonBlankString, relativeScene: String): Deferred<Scene.Id>
	fun createSceneAfter(name: NonBlankString, relativeScene: String): Deferred<Scene.Id>

}