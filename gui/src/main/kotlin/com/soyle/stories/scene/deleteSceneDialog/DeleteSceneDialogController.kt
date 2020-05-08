package com.soyle.stories.scene.deleteSceneDialog

import com.soyle.stories.scene.deleteScene.DeleteSceneController
import com.soyle.stories.scene.items.SceneItemViewModel

class DeleteSceneDialogController(
  private val presenter: DeleteSceneDialogPresenter,
  private val deleteSceneController: DeleteSceneController
) : DeleteSceneDialogViewListener {
	override fun getValidState(sceneItemViewModel: SceneItemViewModel) {
		presenter.displayDeleteSceneDialog(sceneItemViewModel)
	}

	override fun deleteScene(sceneId: String) {
		deleteSceneController.deleteScene(sceneId)
	}
}