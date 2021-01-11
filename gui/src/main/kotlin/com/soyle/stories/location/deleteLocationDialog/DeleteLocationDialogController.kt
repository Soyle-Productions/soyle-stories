package com.soyle.stories.location.deleteLocationDialog

import com.soyle.stories.entities.Location
import com.soyle.stories.location.deleteLocation.DeleteLocationController
import java.util.*

class DeleteLocationDialogController(
	private val deleteLocationController: DeleteLocationController,
	private val presenter: DeleteLocationDialogPresenter
) : DeleteLocationDialogViewListener {

	override fun getValidState(locationId: String, locationName: String) {
		presenter.displayDeleteLocationDialog(locationId, locationName)
	}

	override fun deleteLocation(locationId: String) {
		deleteLocationController.deleteLocation(Location.Id(UUID.fromString(locationId)))
	}
}