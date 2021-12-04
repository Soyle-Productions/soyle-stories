package com.soyle.stories.scene.delete

import javafx.scene.Parent
import javafx.scene.control.Alert
import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType
import tornadofx.*

class DeleteScenePromptView(
	private val viewModel: DeleteScenePromptViewModel
) : Fragment() {

	init {
		title = "Delete Scene"
	}

	private val alert = Alert(Alert.AlertType.CONFIRMATION)

	init {
		alert.resultProperty().onChange {
			viewModel.result().set(it)
		}
	}

	override val root: Parent = alert.dialogPane.apply {
		headerText = "Delete Scene"
		content = vbox {
			label(stringBinding(viewModel.name()) { "Are you sure you want to delete the \"${viewModel.name}\" scene?" })
			checkbox("Do not show this dialog again.") {
				selectedProperty().bindBidirectional(viewModel.doNotShowAgain())
			}
		}
		buttonTypes.setAll(
			ButtonType("Delete", Delete),
			ButtonType("Show Ramifications", Ramifications),
			ButtonType("Cancel", Cancel)
		)
	}

	companion object {
		val Delete = ButtonBar.ButtonData.FINISH
		val Ramifications = ButtonBar.ButtonData.YES
		val Cancel = ButtonBar.ButtonData.CANCEL_CLOSE
	}

}