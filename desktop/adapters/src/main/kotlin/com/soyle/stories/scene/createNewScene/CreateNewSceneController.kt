package com.soyle.stories.scene.createNewScene

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.validation.NonBlankString
import kotlinx.coroutines.Deferred

interface CreateNewSceneController {

	fun createNewScene(name: NonBlankString): Deferred<Scene.Id>

	fun createNewSceneBefore(name: NonBlankString, sceneId: String): Deferred<Scene.Id>
	fun createNewSceneAfter(name: NonBlankString, sceneId: String): Deferred<Scene.Id>

}