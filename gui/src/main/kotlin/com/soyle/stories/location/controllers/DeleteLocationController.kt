package com.soyle.stories.location.controllers

import com.soyle.stories.location.usecases.deleteLocation.DeleteLocation
import java.util.*

class DeleteLocationController(
  private val deleteLocation: DeleteLocation,
  private val deleteLocationOutputPort: DeleteLocation.OutputPort
) {

	suspend fun deleteLocation(locationId: String) {
		deleteLocation.invoke(
		  UUID.fromString(locationId),
		  deleteLocationOutputPort
		)
	}

}