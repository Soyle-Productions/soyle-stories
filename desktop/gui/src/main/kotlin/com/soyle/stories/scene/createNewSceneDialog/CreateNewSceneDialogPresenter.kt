package com.soyle.stories.scene.createNewSceneDialog

import com.soyle.stories.common.Notifier
import com.soyle.stories.common.listensTo
import com.soyle.stories.gui.View
import com.soyle.stories.scene.usecases.createNewScene.CreateNewScene

class CreateNewSceneDialogPresenter(
  private val view: View.Nullable<CreateNewSceneDialogViewModel>,
  createNewSceneNotifier: Notifier<CreateNewScene.OutputPort>
) {

	private val subPresenters = listOf(
	  CreateNewScenePresenter(view) listensTo createNewSceneNotifier
	)

	fun displayCreateNewSceneDialog() {
		view.update {
			CreateNewSceneDialogViewModel(
			  "Create New Scene",
			  "Name",
			  null
			)
		}
	}

}