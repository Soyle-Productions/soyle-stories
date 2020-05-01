package com.soyle.stories.location.deleteLocationDialog

import com.soyle.stories.common.onChangeUntil
import com.soyle.stories.di.get
import com.soyle.stories.location.items.LocationItemViewModel
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.WorkBench
import javafx.scene.control.Alert
import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType
import tornadofx.onChangeOnce

fun deleteLocationDialog(scope: ProjectScope, locationItemViewModel: LocationItemViewModel) {

	val viewListener: DeleteLocationDialogViewListener = scope.get()

	val locationId = locationItemViewModel.id
	val locationName = locationItemViewModel.name

	val confirmButton = ButtonType("Delete", ButtonBar.ButtonData.FINISH)

	val alert = Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this location?", confirmButton, ButtonType.CANCEL)
	alert.title = "Confirm"
	alert.headerText = "Delete $locationName?"
	alert.dialogPane.styleClass.add("deleteLocation")
	scope.get<WorkBench>().currentStage?.also { owner ->
		owner.showingProperty().onChangeUntil({ it != true }) {
			if (it != true) alert.hide()
		}
		alert.initOwner(owner)
	}
	alert.resultProperty().onChangeOnce {
		if (it == confirmButton) viewListener.deleteLocation(locationId)
		alert.close()
	}
	alert.show()
}