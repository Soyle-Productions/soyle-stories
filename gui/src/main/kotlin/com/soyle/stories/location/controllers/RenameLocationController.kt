package com.soyle.stories.location.controllers

import com.soyle.stories.location.usecases.renameLocation.RenameLocation
import java.util.*

class RenameLocationController(
  private val renameLocation: RenameLocation,
  private val renameLocationOutputPort: RenameLocation.OutputPort
) {

	suspend fun renameLocation(locationId: String, newName: String)
	{
		renameLocation.invoke(
		  UUID.fromString(locationId),
		  newName,
		  renameLocationOutputPort
		)
	}

}