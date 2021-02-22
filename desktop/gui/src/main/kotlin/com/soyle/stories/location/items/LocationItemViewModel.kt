package com.soyle.stories.location.items

import com.soyle.stories.domain.location.Location
import com.soyle.stories.usecase.location.listAllLocations.LocationItem

class LocationItemViewModel(val id: Location.Id, val name: String) {
	constructor(locationItem: LocationItem) : this(locationItem.id, locationItem.locationName)
}