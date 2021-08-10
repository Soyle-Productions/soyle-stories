package com.soyle.stories.storyevent.createStoryEventDialog

import com.soyle.stories.common.Notifier
import com.soyle.stories.common.listensTo
import com.soyle.stories.gui.View
import com.soyle.stories.storyevent.createStoryEventDialog.presenters.CreateStoryEventPresenter
import com.soyle.stories.usecase.storyevent.create.CreateStoryEvent

class CreateStoryEventDialogPresenter(
  private val view: View.Nullable<CreateStoryEventDialogViewModel>,
  createStoryEventNotifier: Notifier<CreateStoryEvent.OutputPort>
) {

	private val subPresenters = listOf(
	  CreateStoryEventPresenter(view) listensTo createStoryEventNotifier
	)

	internal fun displayCreateStoryEventDialog()
	{
		view.update {
			CreateStoryEventDialogViewModel(
			  "New Story Event",
			  null,
			  false
			)
		}
	}


}