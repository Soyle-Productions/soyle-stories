package com.soyle.stories.scene.deleteSceneDialog

import com.soyle.stories.common.onChangeUntil
import com.soyle.stories.di.get
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.WorkBench
import com.soyle.stories.scene.items.SceneItemViewModel
import javafx.scene.control.Alert
import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType
import tornadofx.onChangeOnce

fun deleteSceneDialog(scope: ProjectScope, sceneItemViewModel: SceneItemViewModel)
{

	val viewListener = scope.get<DeleteSceneDialogViewListener>()
	val model = scope.get<DeleteSceneDialogModel>()

	model.itemProperty.onChangeOnce { viewModel ->
		if (viewModel == null) return@onChangeOnce

		val confirmButton = ButtonType(viewModel.deleteButtonLabel, ButtonBar.ButtonData.FINISH)
		val cancelButton = ButtonType(viewModel.cancelButtonLabel, ButtonBar.ButtonData.CANCEL_CLOSE)

		val alert = Alert(Alert.AlertType.CONFIRMATION, viewModel.content, confirmButton, cancelButton)
		alert.title = viewModel.title
		alert.headerText = viewModel.header
		alert.dialogPane.styleClass.add("deleteScene")
		scope.get<WorkBench>().currentStage?.also { owner ->
			owner.showingProperty().onChangeUntil({ it != true }) {
				if (it != true) alert.hide()
			}
			alert.initOwner(owner)
		}
		alert.resultProperty().onChangeOnce {
			when (it) {
				confirmButton -> {
					viewListener.deleteScene(sceneItemViewModel.id)
				}
			}
			model.item = null
			alert.close()
		}
		alert.show()
	}

	viewListener.getValidState(sceneItemViewModel)

}