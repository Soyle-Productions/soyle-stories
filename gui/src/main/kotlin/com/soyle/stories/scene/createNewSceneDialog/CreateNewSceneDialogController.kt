package com.soyle.stories.scene.createNewSceneDialog

import com.soyle.stories.common.NonBlankString
import com.soyle.stories.scene.createNewScene.CreateNewSceneController

class CreateNewSceneDialogController(
  private val presenter: CreateNewSceneDialogPresenter,
  private val createNewSceneController: CreateNewSceneController
) : CreateNewSceneDialogViewListener {

	override fun getValidState() {
		presenter.displayCreateNewSceneDialog()
	}

	override fun createScene(name: NonBlankString) {
		createNewSceneController.createNewScene(name)
	}

	override fun createSceneBefore(name: NonBlankString, relativeScene: String) {
		createNewSceneController.createNewSceneBefore(name, relativeScene)
	}

	override fun createSceneAfter(name: NonBlankString, relativeScene: String) {
		createNewSceneController.createNewSceneAfter(name, relativeScene)
	}

}