package com.soyle.stories.scene.reorderSceneDialog

import com.soyle.stories.gui.View


class ReorderSceneDialogPresenter (
  private val view: View.Nullable<ReorderSceneDialogViewModel>
){

	fun displayReorderSceneDialog(sceneName: String)
	{
		view.update {
			ReorderSceneDialogViewModel(
			  title = "Confirm",
			  header = "Reorder $sceneName?",
			  content = "Are you sure you want to reorder this scene?  This may have unintended consequences for the continuity of the story.",
			  reorderButtonLabel = "Reorder",
			  cancelButtonLabel = "Cancel",
			  errorMessage = null
			)
		}
	}
}