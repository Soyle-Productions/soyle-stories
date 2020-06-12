package com.soyle.stories.scene.reorderSceneDialog

import com.soyle.stories.gui.View
import com.soyle.stories.writer.DialogType
import com.soyle.stories.writer.usecases.getDialogPreferences.GetDialogPreferences


class ReorderSceneDialogPresenter (
  private val view: View.Nullable<ReorderSceneDialogViewModel>
) : GetDialogPreferences.OutputPort {

	fun displayReorderSceneDialog(sceneName: String)
	{
		view.update {
			ReorderSceneDialogViewModel(
			  title = "Confirm",
			  header = "Reorder $sceneName?",
			  content = "Are you sure you want to reorder this scene?  This may have unintended consequences for the continuity of the story.",
			  reorderButtonLabel = "Reorder",
			  cancelButtonLabel = "Cancel",
			  showAgainLabel = "Do not show this dialog again.",
			  errorMessage = null,
			  doDefaultAction = null
			)
		}
	}

	override fun gotDialogPreferences(response: GetDialogPreferences.ResponseModel) {
		if (response.dialog != DialogType.ReorderScene.name) return
		view.updateOrInvalidated {
			copy(
			  doDefaultAction = ! response.shouldShow
			)
		}
	}

	override fun failedToGetDialogPreferences(failure: Exception) {
		view.updateOrInvalidated {
			copy(
			  errorMessage = failure.localizedMessage ?: "",
			  doDefaultAction = false
			)
		}
	}
}