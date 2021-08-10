package com.soyle.stories.storyevent.createStoryEventDialog.presenters

import com.soyle.stories.gui.View
import com.soyle.stories.storyevent.createStoryEventDialog.CreateStoryEventDialogViewModel
import com.soyle.stories.usecase.storyevent.create.CreateStoryEvent

class CreateStoryEventPresenter(
  private val view: View.Nullable<CreateStoryEventDialogViewModel>
) : CreateStoryEvent.OutputPort {
	override fun receiveCreateStoryEventResponse(response: CreateStoryEvent.ResponseModel) {
		view.updateOrInvalidated {
			copy(
			  success = true
			)
		}
	}
}