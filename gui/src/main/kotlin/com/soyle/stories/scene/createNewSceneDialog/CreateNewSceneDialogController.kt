package com.soyle.stories.scene.createNewSceneDialog

import com.soyle.stories.scene.createNewScene.CreateNewSceneController

class CreateNewSceneDialogController(
  private val presenter: CreateNewSceneDialogPresenter,
  private val createNewSceneController: CreateNewSceneController
) : CreateNewSceneDialogViewListener {

	override fun getValidState() {
		presenter.displayCreateNewSceneDialog()
	}

	override fun createScene(name: String) {
		createNewSceneController.createNewScene(name)
	}

}