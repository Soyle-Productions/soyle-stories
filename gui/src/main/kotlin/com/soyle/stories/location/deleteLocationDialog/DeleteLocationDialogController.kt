package com.soyle.stories.location.deleteLocationDialog

import com.soyle.stories.gui.ThreadTransformer
import com.soyle.stories.location.controllers.DeleteLocationController

class DeleteLocationDialogController(
  private val threadTransformer: ThreadTransformer,
  private val deleteLocationController: DeleteLocationController,
  private val presenter: DeleteLocationDialogPresenter
) : DeleteLocationDialogViewListener {

	override fun getValidState(locationId: String, locationName: String) {
		presenter.displayDeleteLocationDialog(locationId, locationName)
	}

	override fun deleteLocation(locationId: String) {
		threadTransformer.async {
			deleteLocationController.deleteLocation(locationId)
		}
	}
}