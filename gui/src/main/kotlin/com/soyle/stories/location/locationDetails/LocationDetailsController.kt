package com.soyle.stories.location.locationDetails

import com.soyle.stories.gui.ThreadTransformer
import com.soyle.stories.location.usecases.getLocationDetails.GetLocationDetails
import java.util.*

class LocationDetailsController(
  private val threadTransformer: ThreadTransformer,
  private val locationId: String,
  private val getLocationDetails: GetLocationDetails,
  private val getLocationDetailsOutputPort: GetLocationDetails.OutputPort
) : LocationDetailsViewListener {

	override fun getValidState() {
		threadTransformer.async {
			getLocationDetails.invoke(UUID.fromString(locationId), getLocationDetailsOutputPort)
		}
	}

	override fun reDescribeLocation(newDescription: String) {
	}

}