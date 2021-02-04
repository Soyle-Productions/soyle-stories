package com.soyle.stories.location.locationList

import com.soyle.stories.common.SingleNonBlankLine
import com.soyle.stories.entities.Location

interface LocationListViewListener {

	fun getValidState()
	fun renameLocation(locationId: Location.Id, newName: SingleNonBlankLine)
	fun openLocationDetails(locationId: String)

}