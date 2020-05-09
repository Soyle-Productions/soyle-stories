package com.soyle.stories.scene.deleteSceneDialog

import com.soyle.stories.gui.View
import com.soyle.stories.scene.SceneException
import com.soyle.stories.scene.items.SceneItemViewModel
import com.soyle.stories.scene.usecases.deleteScene.DeleteScene

class DeleteSceneDialogPresenter(
  private val view: View.Nullable<DeleteSceneDialogViewModel>
) : DeleteScene.OutputPort {

	internal fun displayDeleteSceneDialog(sceneItem: SceneItemViewModel)
	{
		view.update {
			DeleteSceneDialogViewModel(
			  title = "Confirm",
			  header = "Delete ${sceneItem.name}?",
			  content = "Are you sure you want to delete this scene?",
			  deleteButtonLabel = "Delete",
			  cancelButtonLabel = "Cancel",
			  errorMessage = null
			)
		}
	}

	override fun receiveDeleteSceneFailure(failure: SceneException) {
		view.updateOrInvalidated {
			copy(
			  errorMessage = failure.localizedMessage.takeIf { it.isNotBlank() } ?: "Failure"
			)
		}
	}

	override fun receiveDeleteSceneResponse(responseModel: DeleteScene.ResponseModel) {

	}

}