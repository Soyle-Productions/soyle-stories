package com.soyle.stories.storyevent.storyEventList.presenters

import com.soyle.stories.domain.storyevent.events.StoryEventRenamed
import com.soyle.stories.gui.View
import com.soyle.stories.storyevent.items.StoryEventListItemViewModel
import com.soyle.stories.storyevent.rename.StoryEventRenamedReceiver
import com.soyle.stories.storyevent.storyEventList.StoryEventListViewModel
import com.soyle.stories.usecase.storyevent.rename.RenameStoryEvent

class RenameStoryEventPresenter(
  private val view: View.Nullable<StoryEventListViewModel>
) : StoryEventRenamedReceiver {

	override suspend fun receiveStoryEventRenamed(event: StoryEventRenamed) {
		val storyEventId = event.storyEventId.uuid.toString()
		view.updateOrInvalidated {
			copy(
			  storyEvents = storyEvents.map {
				  if (it.id == storyEventId) {
					  StoryEventListItemViewModel(it.id, it.ordinal, event.newName)
				  } else it
			  }
			)
		}
	}

}