package com.soyle.stories.storyevent.storyEventList

import com.soyle.stories.common.Notifier
import com.soyle.stories.common.listensTo
import com.soyle.stories.gui.View
import com.soyle.stories.storyevent.items.StoryEventListItemViewModel
import com.soyle.stories.storyevent.storyEventList.presenters.CreateStoryEventPresenter
import com.soyle.stories.storyevent.storyEventList.presenters.RenameStoryEventPresenter
import com.soyle.stories.storyevent.usecases.createStoryEvent.CreateStoryEvent
import com.soyle.stories.storyevent.usecases.listAllStoryEvents.ListAllStoryEvents
import com.soyle.stories.storyevent.usecases.renameStoryEvent.RenameStoryEvent

class StoryEventListPresenter(
  private val view: View.Nullable<StoryEventListViewModel>,
  createStoryEventNotifier: Notifier<CreateStoryEvent.OutputPort>,
  renameStoryEventNotifier: Notifier<RenameStoryEvent.OutputPort>
) : ListAllStoryEvents.OutputPort {

	private val subPresenters = listOf(
	  CreateStoryEventPresenter(view) listensTo createStoryEventNotifier,
	  RenameStoryEventPresenter(view) listensTo renameStoryEventNotifier
	)

	override fun receiveListAllStoryEventsResponse(response: ListAllStoryEvents.ResponseModel) {
		view.update {
			StoryEventListViewModel(
			  "Story Events",
			  "No Story Events have been created yet.",
			  "Create Story Event",
			  response.storyEventItems.map(::StoryEventListItemViewModel).sortedBy { it.ordinal },
			  null
			)
		}
	}

}