package com.soyle.stories.scene.reorder

import javafx.scene.Parent
import javafx.scene.control.Alert
import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType
import tornadofx.*

class ReorderScenePromptView(
	private val viewModel: ReorderScenePromptViewModel
) : Fragment() {

	init {
		title = "Reorder Scene"
	}

	private val alert = Alert(Alert.AlertType.CONFIRMATION)

	override val root: Parent = alert.dialogPane.apply {
		headerText = "Reorder Scene"
		content = vbox {
			label(stringBinding(viewModel.name()) { "Are you sure you want to reorder the \"${viewModel.name}\" scene?" })
			checkbox("Do not show this dialog again.") {
				selectedProperty().bindBidirectional(viewModel.doNotShowAgain())
			}
		}
		buttonTypes.setAll(
			ButtonType("Reorder", Reorder),
			ButtonType("Show Ramifications", Ramifications),
			ButtonType("Cancel", Cancel)
		)
	}

	init {
		viewModel.result().bind(alert.resultProperty())
	}

	companion object {
		val Reorder = ButtonBar.ButtonData.FINISH
		val Ramifications = ButtonBar.ButtonData.YES
		val Cancel = ButtonBar.ButtonData.CANCEL_CLOSE
	}

}