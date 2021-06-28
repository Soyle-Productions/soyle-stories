package com.soyle.stories.scene.sceneList.presenters

import com.soyle.stories.gui.View
import com.soyle.stories.scene.sceneList.SceneListViewModel
import com.soyle.stories.usecase.scene.renameScene.RenameScene

class RenameScenePresenter(
  private val view: View.Nullable<SceneListViewModel>
) : RenameScene.OutputPort {
	override fun receiveRenameSceneResponse(response: RenameScene.ResponseModel) {
		view.updateOrInvalidated {
			copy(
			  scenes = scenes.map {
				  if (it.id == response.sceneId.toString()) {
				  	it.copy(name = response.newName)
				  } else it
			  },
			  renameSceneFailureMessage = null
			)
		}
	}

	override fun receiveRenameSceneFailure(failure: Exception) {
		view.updateOrInvalidated {
			copy(
			  renameSceneFailureMessage = failure.localizedMessage?.takeIf { it.isNotBlank() } ?: "Failure"
			)
		}
	}
}