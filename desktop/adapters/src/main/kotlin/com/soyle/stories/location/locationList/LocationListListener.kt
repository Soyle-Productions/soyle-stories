package com.soyle.stories.location.locationList

import com.soyle.stories.usecase.location.listAllLocations.LocationItem

interface LocationListListener {

	fun receiveLocationListUpdate(locations: List<LocationItem>)

}