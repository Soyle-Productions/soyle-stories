package com.soyle.stories.storyevent.storyEventDetails

import com.soyle.stories.gui.View
import com.soyle.stories.location.items.LocationItemViewModel
import com.soyle.stories.location.usecases.listAllLocations.ListAllLocations

class StoryEventDetailsPresenter(
  private val view: View.Nullable<StoryEventDetailsViewModel>
) : ListAllLocations.OutputPort {


	override fun receiveListAllLocationsResponse(response: ListAllLocations.ResponseModel) {
		view.update {
			if (this != null) copy(locations = response.locations.map(::LocationItemViewModel))
			else {
				StoryEventDetailsViewModel(
				  title = "Story Event Details - [TODO]",
				  locationSelectionButtonLabel = "Select Location",
				  locations = response.locations.map(::LocationItemViewModel)
				)
			}
		}
	}

}