package com.soyle.stories.scene.createNewSceneDialog

import com.soyle.stories.gui.View
import com.soyle.stories.scene.SceneException
import com.soyle.stories.scene.usecases.createNewScene.CreateNewScene

class CreateNewScenePresenter(
  private val view: View.Nullable<CreateNewSceneDialogViewModel>
) : CreateNewScene.OutputPort {

	override fun receiveCreateNewSceneResponse(response: CreateNewScene.ResponseModel) {
		view.updateOrInvalidated {
			copy(
			  errorMessage = null,
			  success = true
			)
		}
	}

	override fun receiveCreateNewSceneFailure(failure: SceneException) {
		view.updateOrInvalidated {
			copy(
			  errorMessage = failure.localizedMessage?.takeUnless { it.isBlank() } ?: "Failure"
			)
		}
	}
}