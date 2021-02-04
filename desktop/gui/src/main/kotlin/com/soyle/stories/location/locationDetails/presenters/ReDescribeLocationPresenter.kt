package com.soyle.stories.location.locationDetails.presenters

import com.soyle.stories.location.LocationException
import com.soyle.stories.location.locationDetails.LocationDetailsView
import com.soyle.stories.location.usecases.redescribeLocation.ReDescribeLocation

class ReDescribeLocationPresenter(
  private val locationId: String,
  private val view: LocationDetailsView
) : ReDescribeLocation.OutputPort {

	override fun receiveReDescribeLocationFailure(failure: LocationException) {
	}

	override fun receiveReDescribeLocationResponse(response: ReDescribeLocation.ResponseModel) {
		if (response.locationId.toString() != locationId) return
		view.updateOrInvalidated {
			copy(
			  description = response.updatedDescription
			)
		}
	}
}