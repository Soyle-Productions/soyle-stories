package com.soyle.stories.storyevent.storyEventList.presenters

import com.soyle.stories.gui.View
import com.soyle.stories.storyevent.StoryEventException
import com.soyle.stories.storyevent.items.StoryEventListItemViewModel
import com.soyle.stories.storyevent.storyEventList.StoryEventListViewModel
import com.soyle.stories.storyevent.usecases.renameStoryEvent.RenameStoryEvent

class RenameStoryEventPresenter(
  private val view: View.Nullable<StoryEventListViewModel>
) : RenameStoryEvent.OutputPort {

	override fun receiveRenameStoryEventResponse(response: RenameStoryEvent.ResponseModel) {
		val storyEventId = response.storyEventId.toString()
		view.updateOrInvalidated {
			copy(
			  storyEvents = storyEvents.map {
				  if (it.id == storyEventId) {
					  StoryEventListItemViewModel(it.id, it.ordinal, response.newName)
				  } else it
			  }
			)
		}
	}

	override fun receiveRenameStoryEventFailure(failure: StoryEventException) {

	}

}