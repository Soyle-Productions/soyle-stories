package com.soyle.stories.location.controllers

import com.soyle.stories.domain.validation.SingleNonBlankLine
import com.soyle.stories.usecase.location.createNewLocation.CreateNewLocation

class CreateNewLocationController(
  private val createNewLocation: CreateNewLocation,
  private val createNewLocationOutputPort: CreateNewLocation.OutputPort
) {

	suspend fun createNewLocation(name: SingleNonBlankLine, description: String) {
		createNewLocation.invoke(name, description.takeIf { it.isNotBlank() }, createNewLocationOutputPort)
	}

}