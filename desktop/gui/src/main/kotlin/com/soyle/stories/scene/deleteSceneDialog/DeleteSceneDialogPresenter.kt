package com.soyle.stories.scene.deleteSceneDialog

import com.soyle.stories.gui.View
import com.soyle.stories.scene.items.SceneItemViewModel
import com.soyle.stories.usecase.scene.deleteScene.DeleteScene
import com.soyle.stories.writer.usecases.DialogPreference
import com.soyle.stories.writer.usecases.getDialogPreferences.GetDialogPreferences

class DeleteSceneDialogPresenter(
  private val view: View.Nullable<DeleteSceneDialogViewModel>
) : DeleteScene.OutputPort, GetDialogPreferences.OutputPort {

	internal fun displayDeleteSceneDialog(sceneItem: SceneItemViewModel)
	{
		view.update {
			DeleteSceneDialogViewModel(
			  title = "Confirm",
			  header = "Delete ${sceneItem.name}?",
			  content = "Are you sure you want to delete this scene?",
			  deleteButtonLabel = "Delete",
			  cancelButtonLabel = "Cancel",
			  errorMessage = null,
			  showAgain = null
			)
		}
	}

	override fun gotDialogPreferences(response: DialogPreference)
	{
		view.updateOrInvalidated {
			copy(
			  showAgain = response.shouldShow
			)
		}
	}

	override fun failedToGetDialogPreferences(failure: Exception) {
		view.updateOrInvalidated {
			copy(
			  showAgain = false
			)
		}
	}

	override fun receiveDeleteSceneFailure(failure: Exception) {
		view.updateOrInvalidated {
			copy(
			  errorMessage = failure.localizedMessage.takeIf { it.isNotBlank() } ?: "Failure"
			)
		}
	}

	override fun receiveDeleteSceneResponse(responseModel: DeleteScene.ResponseModel) {}

}