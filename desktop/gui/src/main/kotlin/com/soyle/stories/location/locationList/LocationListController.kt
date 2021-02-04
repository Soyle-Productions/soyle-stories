package com.soyle.stories.location.locationList

import com.soyle.stories.common.SingleNonBlankLine
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.common.isListeningTo
import com.soyle.stories.entities.Location
import com.soyle.stories.layout.openTool.OpenToolController
import com.soyle.stories.location.controllers.RenameLocationController

class LocationListController(
  private val threadTransformer: ThreadTransformer,
  private val liveLocationList: LiveLocationList,
  private val locationListListener: LocationListListener,
  private val renameLocationController: RenameLocationController,
  private val openToolController: OpenToolController
) : LocationListViewListener {

	override fun getValidState() {
		if (locationListListener isListeningTo liveLocationList) {
			liveLocationList.removeListener(locationListListener)
		}
		liveLocationList.addListener(locationListListener)
	}

	override fun renameLocation(locationId: Location.Id, newName: SingleNonBlankLine) {
		threadTransformer.async {
			renameLocationController.renameLocation(locationId, newName)
		}
	}

	override fun openLocationDetails(locationId: String) {
		openToolController.openLocationDetailsTool(locationId)
	}
}