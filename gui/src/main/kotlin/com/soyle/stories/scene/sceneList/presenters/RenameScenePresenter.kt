package com.soyle.stories.scene.sceneList.presenters

import com.soyle.stories.gui.View
import com.soyle.stories.scene.SceneException
import com.soyle.stories.scene.items.SceneItemViewModel
import com.soyle.stories.scene.sceneList.SceneListViewModel
import com.soyle.stories.scene.usecases.renameScene.RenameScene

class RenameScenePresenter(
  private val view: View.Nullable<SceneListViewModel>
) : RenameScene.OutputPort {
	override fun receiveRenameSceneResponse(response: RenameScene.ResponseModel) {
		view.updateOrInvalidated {
			copy(
			  scenes = scenes.filterNot { it.id == response.sceneId.toString() } + SceneItemViewModel(response.sceneId.toString(), response.newName),
			  renameSceneFailureMessage = null
			)
		}
	}

	override fun receiveRenameSceneFailure(failure: SceneException) {
		view.updateOrInvalidated {
			copy(
			  renameSceneFailureMessage = failure.localizedMessage?.takeIf { it.isNotBlank() } ?: "Failure"
			)
		}
	}
}