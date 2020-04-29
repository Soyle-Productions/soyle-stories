package com.soyle.stories.location.locationList

import com.soyle.stories.gui.ThreadTransformer
import com.soyle.stories.location.LocationComponent
import com.soyle.stories.project.layout.openTool.OpenToolController

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
		  locationComponent.renameLocationController,
		  object : OpenToolController {
			  override fun openLocationDetailsTool(locationId: String) {
			  }

			  override fun openBaseStoryStructureTool(themeId: String, characterId: String) {

			  }
		  }
		)
	}
}