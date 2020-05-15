package com.soyle.stories.storyevent.storyEventDetails

import com.soyle.stories.common.Notifier
import com.soyle.stories.common.listensTo
import com.soyle.stories.gui.View
import com.soyle.stories.location.items.LocationItemViewModel
import com.soyle.stories.location.usecases.listAllLocations.ListAllLocations
import com.soyle.stories.storyevent.storyEventDetails.presenters.LinkLocationToStoryEventPresenter
import com.soyle.stories.storyevent.usecases.linkLocationToStoryEvent.LinkLocationToStoryEvent

class StoryEventDetailsPresenter(
  private val view: View.Nullable<StoryEventDetailsViewModel>,
  linkLocationToStoryEventNotifier: Notifier<LinkLocationToStoryEvent.OutputPort>
) : ListAllLocations.OutputPort {

	private var selectedLocationId: String? = null

	private val subPresenters = listOf(
	  LinkLocationToStoryEventPresenter(view) listensTo linkLocationToStoryEventNotifier
	)

	override fun receiveListAllLocationsResponse(response: ListAllLocations.ResponseModel) {
		view.update {

			val locations = response.locations.map(::LocationItemViewModel)

			if (this != null) copy(locations = locations)
			else {
				StoryEventDetailsViewModel(
				  title = "Story Event Details - [TODO]",
				  locationSelectionButtonLabel = "Select Location",
				  selectedLocation = selectedLocationId?.let { id -> locations.find { it.id == id } },
				  locations = locations
				)
			}
		}
	}

}