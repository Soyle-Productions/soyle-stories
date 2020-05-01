package com.soyle.stories.location.locationList

import com.soyle.stories.gui.ThreadTransformer
import com.soyle.stories.location.controllers.RenameLocationController
import com.soyle.stories.location.usecases.listAllLocations.ListAllLocations
import com.soyle.stories.project.layout.openTool.OpenToolController

class LocationListController(
  private val threadTransformer: ThreadTransformer,
  private val listAllLocations: ListAllLocations,
  private val listAllLocationsOutputPort: ListAllLocations.OutputPort,
  private val renameLocationController: RenameLocationController,
  private val openToolController: OpenToolController
) : LocationListViewListener {

	override fun getValidState() {
		threadTransformer.async {
			listAllLocations.invoke(listAllLocationsOutputPort)
		}
	}

	override fun renameLocation(locationId: String, newName: String) {
		threadTransformer.async {
			renameLocationController.renameLocation(locationId, newName)
		}
	}

	override fun openLocationDetails(locationId: String) {
		openToolController.openLocationDetailsTool(locationId)
	}
}