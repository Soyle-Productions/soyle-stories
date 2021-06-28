package com.soyle.stories.scene.createNewSceneDialog

import com.soyle.stories.gui.View
import com.soyle.stories.usecase.scene.createNewScene.CreateNewScene
import com.soyle.stories.usecase.storyevent.createStoryEvent.CreateStoryEvent

class CreateNewScenePresenter(
  private val view: View.Nullable<CreateNewSceneDialogViewModel>
) : CreateNewScene.OutputPort {

	override val createStoryEventOutputPort: CreateStoryEvent.OutputPort
		get() = error("$this does not supply create story event output port")

	override fun receiveCreateNewSceneResponse(response: CreateNewScene.ResponseModel) {
		view.updateOrInvalidated {
			copy(
			  errorMessage = null,
			  success = true
			)
		}
	}

	override fun receiveCreateNewSceneFailure(failure: Exception) {
		view.updateOrInvalidated {
			copy(
			  errorMessage = failure.localizedMessage?.takeUnless { it.isBlank() } ?: "Failure"
			)
		}
	}
}