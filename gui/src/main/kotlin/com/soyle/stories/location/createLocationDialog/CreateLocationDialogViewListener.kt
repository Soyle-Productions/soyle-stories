package com.soyle.stories.location.createLocationDialog

interface CreateLocationDialogViewListener {

	fun getValidState()
	fun createLocation(name: String, description: String)

}