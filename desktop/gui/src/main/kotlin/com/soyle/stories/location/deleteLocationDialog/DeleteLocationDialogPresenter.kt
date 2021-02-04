package com.soyle.stories.location.deleteLocationDialog

class DeleteLocationDialogPresenter(
  private val view: DeleteLocationDialogView
) {

	fun displayDeleteLocationDialog(locationId: String, locationName: String) {
		view.update {
			DeleteLocationDialogViewModel(
			  locationId,
			  locationName
			)
		}
	}

}