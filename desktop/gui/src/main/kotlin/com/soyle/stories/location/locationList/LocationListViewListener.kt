package com.soyle.stories.location.locationList

import com.soyle.stories.domain.validation.SingleNonBlankLine
import com.soyle.stories.domain.location.Location

interface LocationListViewListener {

	fun getValidState()
	fun renameLocation(locationId: Location.Id, newName: SingleNonBlankLine)
	fun openLocationDetails(locationId: String)

}