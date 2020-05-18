package com.soyle.stories.location.locationList

import com.soyle.stories.location.items.LocationItemViewModel
import com.soyle.stories.location.usecases.listAllLocations.LocationItem

class LocationListPresenter(
  private val view: LocationListView
) : LocationListListener {

	override fun receiveLocationListUpdate(locations: List<LocationItem>) {
		view.update {
			LocationListViewModel(locations.map(::LocationItemViewModel).sortedBy { it.name })
		}
	}
}