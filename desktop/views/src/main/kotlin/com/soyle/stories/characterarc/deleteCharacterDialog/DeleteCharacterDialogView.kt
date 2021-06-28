package com.soyle.stories.characterarc.deleteCharacterDialog

import com.soyle.stories.usecase.character.removeCharacterFromStory.RemoveCharacterFromStory
import com.soyle.stories.common.onChangeUntil
import com.soyle.stories.di.resolve
import com.soyle.stories.project.ProjectScope
import javafx.scene.Parent
import javafx.scene.control.Alert
import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType
import javafx.stage.Modality
import javafx.stage.StageStyle
import tornadofx.*

class DeleteCharacterDialogView : Fragment() {

    override val scope: ProjectScope = super.scope as ProjectScope

    private val viewListener = resolve<DeleteCharacterDialogViewListener>()
    private val model = resolve<DeleteCharacterDialogState>()

    private val alert = Alert(Alert.AlertType.CONFIRMATION)

    override val root: Parent = alert.dialogPane.apply {
        headerTextProperty().bind(model.message)
        content = vbox {
            /*checkbox {
                textProperty().bind(model.doNotShowLabel)
                selectedProperty().bindBidirectional(model.doDefaultAction)
            }*/
        }
        model.itemProperty().onChange { viewModel ->
            if (viewModel == null) {
                buttonTypes.clear()
                return@onChange
            }
            buttonTypes.setAll(
                ButtonType(viewModel.deleteButtonLabel, Delete),
                ButtonType(viewModel.cancelButtonLabel, Cancel)
            )
        }
    }

    init {
        titleProperty.bind(model.title)
        model.itemProperty().onChangeUntil({ it?.doDefaultAction != null }) {
            if (it?.doDefaultAction == false) {
                openModal(StageStyle.DECORATED, Modality.APPLICATION_MODAL)
            } else if (it?.doDefaultAction == true) {
                alert.result = ButtonType("", Delete)
            }
        }
    }

    fun show(request: RemoveCharacterFromStory.ConfirmationRequest) {
        alert.resultProperty().onChangeOnce {
            when (it?.buttonData) {
                Delete -> viewListener.deleteCharacter(request.characterId, ! model.doDefaultAction.value)
                else -> {}
            }
            close()
        }
        viewListener.getValidState(request)
    }

    companion object {
        private inline val Delete get() = ButtonBar.ButtonData.FINISH
        private inline val Cancel get() = ButtonBar.ButtonData.CANCEL_CLOSE
    }
}