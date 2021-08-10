package com.soyle.stories.storyevent.storyEventList.presenters

import com.soyle.stories.gui.View
import com.soyle.stories.storyevent.items.StoryEventListItemViewModel
import com.soyle.stories.storyevent.storyEventList.StoryEventListViewModel
import com.soyle.stories.usecase.storyevent.StoryEventItem
import com.soyle.stories.usecase.storyevent.create.CreateStoryEvent

internal class CreateStoryEventPresenter(
  private val view: View.Nullable<StoryEventListViewModel>
) : CreateStoryEvent.OutputPort {

	override fun receiveCreateStoryEventResponse(response: CreateStoryEvent.ResponseModel) {
		view.updateOrInvalidated {

			val newItem = StoryEventListItemViewModel(StoryEventItem(
				response.createdStoryEvent.storyEventId.uuid,
				response.createdStoryEvent.name,
				response.createdStoryEvent.time.toInt()
			))

			copy(
			  storyEvents = (storyEvents + newItem).sortedBy { it.ordinal }
			)
		}
	}
}