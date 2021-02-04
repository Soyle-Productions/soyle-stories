package com.soyle.stories.location.locationList

import com.soyle.stories.location.usecases.listAllLocations.LocationItem

interface LocationListListener {

	fun receiveLocationListUpdate(locations: List<LocationItem>)

}