package com.soyle.stories.scene.reorderSceneDialog

import com.soyle.stories.common.onChangeUntil
import com.soyle.stories.di.resolve
import javafx.scene.Parent
import javafx.scene.control.Alert
import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType
import javafx.stage.Modality
import javafx.stage.StageStyle
import tornadofx.*
import kotlin.properties.Delegates

class ReorderSceneDialog : Fragment() {

	val viewListener = resolve<ReorderSceneDialogViewListener>()
	val model = resolve<ReorderSceneDialogModel>()

	private lateinit var sceneId: String
	private var index by Delegates.notNull<Int>()

	private val alert = Alert(Alert.AlertType.CONFIRMATION)

	override val root: Parent = alert.dialogPane.apply {
		headerTextProperty().bind(model.header)
		content = vbox {
			label(model.content)
			checkbox {
				textProperty().bind(model.showAgainLabel)
				selectedProperty().bindBidirectional(model.showAgain)
			}
		}
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
		alert.resultProperty().onChangeOnce {
			when (it?.buttonData) {
				ButtonBar.ButtonData.FINISH -> viewListener.reorderScene(sceneId, index, ! model.showAgain.value)
				ButtonBar.ButtonData.YES -> {}
				else -> {}
			}
			close()
		}
		model.itemProperty.onChangeUntil({ it?.doDefaultAction != null }) {
			if (it?.doDefaultAction == false) {
				openModal(StageStyle.DECORATED, Modality.APPLICATION_MODAL)
			} else if (it?.doDefaultAction == true) {
				alert.result = ButtonType("", ButtonBar.ButtonData.FINISH)
			}
		}
	}

	fun show(sceneId: String, sceneName: String, index: Int)
	{
		this.sceneId = sceneId
		this.index = index
		viewListener.getValidState(sceneId, sceneName, index)
	}

}