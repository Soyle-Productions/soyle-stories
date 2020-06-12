package com.soyle.stories.scene.reorderSceneDialog

class ReorderSceneDialogController(
  private val presenter: ReorderSceneDialogPresenter
) : ReorderSceneDialogViewListener {

	override fun getValidState(sceneId: String, sceneName: String, index: Int) {
		presenter.displayReorderSceneDialog(sceneName)
	}

	override fun reorderScene(sceneId: String, index: Int, showNextTime: Boolean) {
	}

}