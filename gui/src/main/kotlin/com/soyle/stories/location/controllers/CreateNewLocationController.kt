package com.soyle.stories.location.controllers

import com.soyle.stories.location.usecases.createNewLocation.CreateNewLocation

class CreateNewLocationController(
  private val createNewLocation: CreateNewLocation,
  private val createNewLocationOutputPort: CreateNewLocation.OutputPort
) {

	suspend fun createNewLocation(name: String, description: String) {
		createNewLocation.invoke(name, description.takeIf { it.isNotBlank() }, createNewLocationOutputPort)
	}

}