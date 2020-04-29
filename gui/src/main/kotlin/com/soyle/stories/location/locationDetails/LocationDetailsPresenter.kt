package com.soyle.stories.location.locationDetails

import com.soyle.stories.eventbus.listensTo
import com.soyle.stories.location.LocationException
import com.soyle.stories.location.events.LocationEvents
import com.soyle.stories.location.locationDetails.presenters.ReDescribeLocationPresenter
import com.soyle.stories.location.usecases.getLocationDetails.GetLocationDetails

class LocationDetailsPresenter(
  locationId: String,
  private val view: LocationDetailsView,
  locationEvents: LocationEvents
) : GetLocationDetails.OutputPort {

	private val subPresenters = listOf(
	  ReDescribeLocationPresenter(locationId, view) listensTo locationEvents.reDescribeLocation
	)


	override fun receiveGetLocationDetailsResponse(response: GetLocationDetails.ResponseModel) {
		view.update {
			LocationDetailsViewModel(
			  toolName = "Location Details - ${response.locationName}",
			  descriptionLabel = "Description",
			  description = response.locationDescription
			)
		}
	}

	override fun receiveGetLocationDetailsFailure(failure: LocationException) {

	}
}