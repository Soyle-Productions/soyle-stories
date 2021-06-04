package com.soyle.stories.location.locationDetails.presenters

import com.soyle.stories.domain.location.Location
import com.soyle.stories.location.locationDetails.LocationDetailsView
import com.soyle.stories.location.locationDetails.LocationDetailsViewModel
import com.soyle.stories.usecase.location.redescribeLocation.ReDescribeLocation

class ReDescribeLocationPresenter(
	private val locationId: Location.Id,
	private val view: LocationDetailsViewModel
) : ReDescribeLocation.OutputPort {

	override fun receiveReDescribeLocationFailure(failure: Exception) = Unit

	override fun receiveReDescribeLocationResponse(response: ReDescribeLocation.ResponseModel) {
		with(view) {
			update {
				if (response.locationId != locationId.uuid) return@update
				description = response.updatedDescription
			}
		}
	}
}
