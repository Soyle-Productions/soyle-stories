package com.soyle.stories.location.locationList

class LocationListViewModel(
  val locations: List<LocationItemViewModel>
) {
	val hasLocations: Boolean
		get() = locations.isNotEmpty()
}