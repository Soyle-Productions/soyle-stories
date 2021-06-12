package com.soyle.stories.scene.createNewSceneDialog

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.scene.createNewScene.CreateNewSceneController
import kotlinx.coroutines.Deferred

class CreateNewSceneDialogController(
  private val presenter: CreateNewSceneDialogPresenter,
  private val createNewSceneController: CreateNewSceneController
) : CreateNewSceneDialogViewListener {

	override fun getValidState() {
		presenter.displayCreateNewSceneDialog()
	}

	override fun createScene(name: NonBlankString): Deferred<Scene.Id> {
		return createNewSceneController.createNewScene(name)
	}

	override fun createSceneBefore(name: NonBlankString, relativeScene: String): Deferred<Scene.Id> {
		return createNewSceneController.createNewSceneBefore(name, relativeScene)
	}

	override fun createSceneAfter(name: NonBlankString, relativeScene: String): Deferred<Scene.Id> {
		return createNewSceneController.createNewSceneAfter(name, relativeScene)
	}

}