package com.soyle.stories.location.locationDetails

import com.soyle.stories.location.LocationException
import com.soyle.stories.location.usecases.getLocationDetails.GetLocationDetails

class LocationDetailsPresenter(
  private val view: LocationDetailsView
) : GetLocationDetails.OutputPort {

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