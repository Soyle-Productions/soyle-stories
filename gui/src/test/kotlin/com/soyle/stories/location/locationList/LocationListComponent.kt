package com.soyle.stories.location.locationList

import com.soyle.stories.gui.ThreadTransformer
import com.soyle.stories.location.LocationComponent
import com.soyle.stories.location.usecases.listAllLocations.ListAllLocations

class LocationListComponent(
  threadTransformer: ThreadTransformer,
  locationComponent: LocationComponent,
  locationListView: () -> LocationListView
) {

	val locationListViewListener: LocationListViewListener by lazy {
		LocationListController(
		  threadTransformer,
		  locationComponent.listAllLocations,
		  LocationListPresenter(
			locationListView(),
			locationComponent.locationEvents
		  ),
		  locationComponent.renameLocationController
		)
	}
}