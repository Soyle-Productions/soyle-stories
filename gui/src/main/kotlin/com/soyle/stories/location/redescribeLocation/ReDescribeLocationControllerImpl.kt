package com.soyle.stories.location.redescribeLocation

import com.soyle.stories.gui.ThreadTransformer
import com.soyle.stories.location.usecases.redescribeLocation.ReDescribeLocation
import java.util.*

class ReDescribeLocationControllerImpl(
  private val threadTransformer: ThreadTransformer,
  private val reDescribeLocation: ReDescribeLocation,
  private val reDescribeLocationOutputPort: ReDescribeLocation.OutputPort
) : ReDescribeLocationController {

	override fun reDescribeLocation(locationId: String, description: String) {
		val validatedLocationId = prepareLocationId(locationId)
		threadTransformer.async {
			reDescribeLocation.invoke(
			  validatedLocationId,
			  description,
			  reDescribeLocationOutputPort
			)
		}
	}

	private fun prepareLocationId(locationId: String): UUID
	{
		if (locationId.isBlank()) throw IllegalArgumentException("Location id cannot be blank")
		return try {
			UUID.fromString(locationId)
		} catch (i: IllegalArgumentException) {
			throw IllegalArgumentException("Location id is invalid")
		}
	}
}