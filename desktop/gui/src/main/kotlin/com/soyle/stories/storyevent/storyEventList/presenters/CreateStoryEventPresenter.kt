package com.soyle.stories.storyevent.storyEventList.presenters

import com.soyle.stories.domain.storyevent.events.StoryEventCreated
import com.soyle.stories.gui.View
import com.soyle.stories.storyevent.create.StoryEventCreatedReceiver
import com.soyle.stories.storyevent.items.StoryEventListItemViewModel
import com.soyle.stories.storyevent.storyEventList.StoryEventListViewModel
import com.soyle.stories.usecase.storyevent.StoryEventItem
import com.soyle.stories.usecase.storyevent.create.CreateStoryEvent

internal class CreateStoryEventPresenter(
  private val view: View.Nullable<StoryEventListViewModel>
) : StoryEventCreatedReceiver {

	override suspend fun receiveStoryEventCreated(event: StoryEventCreated) {
		view.updateOrInvalidated {

			val newItem = StoryEventListItemViewModel(StoryEventItem(
				event.storyEventId,
				event.name,
				event.time.toLong(),
				null
			))

			copy(
			  storyEvents = (storyEvents + newItem)
			)
		}
	}
}