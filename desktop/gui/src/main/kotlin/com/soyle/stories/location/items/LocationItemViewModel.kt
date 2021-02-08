package com.soyle.stories.location.items

import com.soyle.stories.usecase.location.listAllLocations.LocationItem

class LocationItemViewModel(val id: String, val name: String) {
	constructor(locationItem: LocationItem) : this(locationItem.id.toString(), locationItem.locationName)
}