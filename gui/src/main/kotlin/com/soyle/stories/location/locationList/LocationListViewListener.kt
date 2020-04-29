package com.soyle.stories.location.locationList

interface LocationListViewListener {

	fun getValidState()
	fun renameLocation(locationId: String, newName: String)
	fun openLocationDetails(locationId: String)

}