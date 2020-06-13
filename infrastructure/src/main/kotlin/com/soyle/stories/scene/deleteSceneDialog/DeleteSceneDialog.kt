package com.soyle.stories.scene.deleteSceneDialog

import com.soyle.stories.common.onChangeUntil
import com.soyle.stories.di.get
import com.soyle.stories.di.resolve
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.scene.items.SceneItemViewModel
import javafx.scene.Parent
import javafx.scene.control.Alert
import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType
import javafx.stage.Modality
import javafx.stage.StageStyle
import tornadofx.*

class DeleteSceneDialog : Fragment() {

	val viewListener = resolve<DeleteSceneDialogViewListener>()
	val model = resolve<DeleteSceneDialogModel>()

	private lateinit var sceneId: String

	private val alert = Alert(Alert.AlertType.CONFIRMATION)

	override val root: Parent = alert.dialogPane.apply {
		headerTextProperty().bind(model.header)
		content = vbox {
			label(model.content)
			checkbox("Do not show this dialog again.") {
				model.showAgain.onChange {
					if (it != null) isSelected = ! it
				}
				selectedProperty().onChange {
					model.showAgain.value = ! it
				}
			}
		}
		model.itemProperty.onChange { viewModel ->
			if (viewModel == null) {
				buttonTypes.clear()
				return@onChange
			}
			buttonTypes.setAll(
			  ButtonType(viewModel.deleteButtonLabel, ButtonBar.ButtonData.FINISH),
			  //val ramificationsButton = ButtonType("Show Ramifications", ButtonBar.ButtonData.YES)
			  ButtonType(viewModel.cancelButtonLabel, ButtonBar.ButtonData.CANCEL_CLOSE)
			)
		}
	}

	init {
		titleProperty.bind(model.title)
		alert.resultProperty().onChangeOnce {
			when (it?.buttonData) {
				ButtonBar.ButtonData.FINISH -> viewListener.deleteScene(sceneId, model.showAgain.value)
				ButtonBar.ButtonData.YES -> {}
				else -> {}
			}
			close()
		}
		model.itemProperty.onChangeUntil({ it?.showAgain != null }) {
			if (it?.showAgain == true) {
				openModal(StageStyle.DECORATED, Modality.APPLICATION_MODAL)
			} else if (it?.showAgain == false) {
				alert.result = ButtonType("", ButtonBar.ButtonData.FINISH)
			}
		}
	}

	fun show(sceneItemViewModel: SceneItemViewModel)
	{
		sceneId = sceneItemViewModel.id
		viewListener.getValidState(sceneItemViewModel)
	}

}

fun deleteSceneDialog(scope: ProjectScope, sceneItemViewModel: SceneItemViewModel)
{

	scope.get<DeleteSceneDialog>()
	  .show(sceneItemViewModel)

}