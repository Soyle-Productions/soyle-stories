package com.soyle.stories.storyevent.storyEventList.presenters

import com.soyle.stories.gui.View
import com.soyle.stories.storyevent.StoryEventException
import com.soyle.stories.storyevent.items.StoryEventListItemViewModel
import com.soyle.stories.storyevent.storyEventList.StoryEventListViewModel
import com.soyle.stories.storyevent.usecases.createStoryEvent.CreateStoryEvent

internal class CreateStoryEventPresenter(
  private val view: View.Nullable<StoryEventListViewModel>
) : CreateStoryEvent.OutputPort {

	override fun receiveCreateStoryEventResponse(response: CreateStoryEvent.ResponseModel) {
		view.updateOrInvalidated {

			val newItem = StoryEventListItemViewModel(response.newItem)
			val itemsById = storyEvents.associateBy { it.id }
			val updatedItems = response.updatedStoryEvents.map {
				val original = itemsById[it.storyEventId.toString()] ?: StoryEventListItemViewModel(it)
				StoryEventListItemViewModel(original.id, it.influenceOrderIndex, it.storyEventName)
			}.associateBy { it.id }
			val unsortedItems = updatedItems.values + itemsById.filterKeys { it !in updatedItems }.values + newItem

			copy(
			  storyEvents = unsortedItems.sortedBy { it.ordinal }
			)
		}
	}

	override fun receiveCreateStoryEventFailure(failure: StoryEventException) {
		// no-op
	}
}