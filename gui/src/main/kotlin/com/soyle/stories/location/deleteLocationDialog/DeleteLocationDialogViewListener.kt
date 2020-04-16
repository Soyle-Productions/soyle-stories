package com.soyle.stories.location.deleteLocationDialog

interface DeleteLocationDialogViewListener {

	fun getValidState(locationId: String, locationName: String)
	fun deleteLocation(locationId: String)

}