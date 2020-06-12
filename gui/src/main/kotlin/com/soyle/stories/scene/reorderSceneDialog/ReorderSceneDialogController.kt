package com.soyle.stories.scene.reorderSceneDialog

import com.soyle.stories.scene.reorderScene.ReorderSceneController

class ReorderSceneDialogController(
  private val presenter: ReorderSceneDialogPresenter,
  private val reorderSceneController: ReorderSceneController
) : ReorderSceneDialogViewListener {

	override fun getValidState(sceneId: String, sceneName: String, index: Int) {
		presenter.displayReorderSceneDialog(sceneName)
	}

	override fun reorderScene(sceneId: String, index: Int, showNextTime: Boolean) {
		reorderSceneController.reorderScene(sceneId, index)
	}

}