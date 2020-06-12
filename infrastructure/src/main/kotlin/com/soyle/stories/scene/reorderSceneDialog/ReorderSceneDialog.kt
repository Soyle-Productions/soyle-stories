package com.soyle.stories.scene.reorderSceneDialog

import com.soyle.stories.di.resolve
import javafx.scene.Parent
import javafx.scene.control.Alert
import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType
import javafx.stage.Modality
import javafx.stage.StageStyle
import tornadofx.Fragment
import tornadofx.onChange
import tornadofx.onChangeOnce

class ReorderSceneDialog : Fragment() {

	val viewListener = resolve<ReorderSceneDialogViewListener>()
	val model = resolve<ReorderSceneDialogModel>()

	private val alert = Alert(Alert.AlertType.CONFIRMATION)

	override val root: Parent = alert.dialogPane.apply {
		headerTextProperty().bind(model.header)
		contentTextProperty().bind(model.content)
		model.itemProperty.onChange { viewModel ->
			if (viewModel == null) {
				buttonTypes.clear()
				return@onChange
			}
			buttonTypes.setAll(
			  ButtonType(viewModel.reorderButtonLabel, ButtonBar.ButtonData.FINISH),
			//val ramificationsButton = ButtonType("Show Ramifications", ButtonBar.ButtonData.YES)
				ButtonType(viewModel.cancelButtonLabel, ButtonBar.ButtonData.CANCEL_CLOSE)
			)
		}
	}

	init {
		titleProperty.bind(model.title)
		model.itemProperty.onChangeOnce {
			openModal(StageStyle.DECORATED, Modality.APPLICATION_MODAL)
		}
		alert.resultProperty().onChangeOnce {
			when (it?.buttonData) {
				ButtonBar.ButtonData.FINISH -> {}
				ButtonBar.ButtonData.CANCEL_CLOSE -> {}
				else -> {}
			}
			close()
		}
	}

	fun show(sceneId: String, sceneName: String, index: Int)
	{
		viewListener.getValidState(sceneId, sceneName, index)
	}

}