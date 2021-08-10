package com.soyle.stories.storyevent.createStoryEventDialog.presenters

import com.soyle.stories.domain.storyevent.events.StoryEventCreated
import com.soyle.stories.gui.View
import com.soyle.stories.storyevent.create.StoryEventCreatedReceiver
import com.soyle.stories.storyevent.createStoryEventDialog.CreateStoryEventDialogViewModel
import com.soyle.stories.usecase.storyevent.create.CreateStoryEvent

class CreateStoryEventPresenter(
  private val view: View.Nullable<CreateStoryEventDialogViewModel>
) : StoryEventCreatedReceiver {

	override suspend fun receiveStoryEventCreated(event: StoryEventCreated) {
		view.updateOrInvalidated {
			copy(
			  success = true
			)
		}
	}
}