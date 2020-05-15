package com.soyle.stories.storyevent.storyEventDetails.presenters

import com.soyle.stories.gui.View
import com.soyle.stories.storyevent.storyEventDetails.StoryEventDetailsViewModel
import com.soyle.stories.storyevent.usecases.linkLocationToStoryEvent.LinkLocationToStoryEvent

internal class LinkLocationToStoryEventPresenter(
  private val view: View.Nullable<StoryEventDetailsViewModel>
) : LinkLocationToStoryEvent.OutputPort {

	override fun receiveLinkLocationToStoryEventResponse(response: LinkLocationToStoryEvent.ResponseModel) {
		view.updateOrInvalidated {
			copy(
			  selectedLocation = response.locationId?.let {
				  val id = it.toString()
				  locations.find { it.id == id }
			  }
			)
		}
	}

	override fun receiveLinkLocationToStoryEventFailure(failure: Exception) {

	}
}