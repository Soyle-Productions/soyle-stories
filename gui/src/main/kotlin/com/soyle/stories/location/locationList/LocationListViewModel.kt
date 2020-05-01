package com.soyle.stories.location.locationList

import com.soyle.stories.location.items.LocationItemViewModel

class LocationListViewModel(
  val locations: List<LocationItemViewModel>
) {
	val hasLocations: Boolean
		get() = locations.isNotEmpty()
}