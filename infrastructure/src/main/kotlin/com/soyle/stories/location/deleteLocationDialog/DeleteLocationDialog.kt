package com.soyle.stories.location.deleteLocationDialog

import com.soyle.stories.di.get
import com.soyle.stories.location.items.LocationItemViewModel
import com.soyle.stories.project.ProjectScope
import javafx.beans.property.SimpleStringProperty
import javafx.scene.Parent
import javafx.scene.control.Alert
import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType
import tornadofx.Fragment
import tornadofx.onChangeOnce

class DeleteLocationDialog : Fragment() {

	private val viewListener: DeleteLocationDialogViewListener = scope.get()

	private val headerText = SimpleStringProperty("")

	private val confirmButton = ButtonType("Delete", ButtonBar.ButtonData.FINISH)

	private val alert = Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this location?", confirmButton, ButtonType.CANCEL)

	fun show( locationItemViewModel: LocationItemViewModel) {
		headerText.value = "Delete ${locationItemViewModel.name}?"
		alert.resultProperty().onChangeOnce {
			if (it == confirmButton) viewListener.deleteLocation(locationItemViewModel.id)
			close()
		}
		openWindow()
	}

	override val root: Parent = alert.apply {
		title = "Confirm"
		headerTextProperty().bind(this@DeleteLocationDialog.headerText)
	}.dialogPane
}

fun deleteLocationDialog(scope: ProjectScope, locationItemViewModel: LocationItemViewModel) {
	scope.get<DeleteLocationDialog>().show(locationItemViewModel)
}