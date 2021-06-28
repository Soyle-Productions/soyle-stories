package com.soyle.stories.storyevent.storyEventDetails.presenters

import com.soyle.stories.gui.View
import com.soyle.stories.storyevent.storyEventDetails.StoryEventDetailsViewModel
import com.soyle.stories.usecase.storyevent.linkLocationToStoryEvent.LinkLocationToStoryEvent
import java.util.*

internal class LinkLocationToStoryEventPresenter(
  private val storyEventId: UUID,
  private val view: View.Nullable<StoryEventDetailsViewModel>
) : LinkLocationToStoryEvent.OutputPort {

	override fun receiveLinkLocationToStoryEventResponse(response: LinkLocationToStoryEvent.ResponseModel) {
		if (response.storyEventId != storyEventId) return
		view.updateOrInvalidated {

			val selectedLocationId = response.locationId

			copy(
			  selectedLocationId = selectedLocationId?.toString(),
			  selectedLocation = selectedLocationId?.let {
				  locations.find { it.id.uuid == selectedLocationId }
			  }
			)
		}
	}

	override fun receiveLinkLocationToStoryEventFailure(failure: Exception) {

	}
}