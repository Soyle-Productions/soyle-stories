package com.soyle.stories.location.deleteLocationDialog

import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType
import tornadofx.UIComponent
import tornadofx.confirm

fun UIComponent.deleteLocationDialog() {

	val viewListener = find<DeleteLocationDialogComponent>().deleteLocationDialogViewListener
	val model = find<DeleteLocationDialogModel>()
	viewListener.getValidState(model.dialog.value.locationId, model.dialog.value.locationName)
	val locationId = model.locationId.value
	val locationName = model.locationName.value

	confirm(
	  header = "Delete $locationName?",
	  content = "Are you sure you want to delete this location?",
	  confirmButton = ButtonType("Delete", ButtonBar.ButtonData.APPLY),
	  cancelButton = ButtonType.CANCEL,
	  owner = currentStage,
	  title = "Confirm"
	) {
		viewListener.deleteLocation(locationId)
	}
}