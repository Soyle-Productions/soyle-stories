package com.soyle.stories.character.delete

import com.soyle.stories.character.removeCharacterFromStory.RemoveCharacterFromStoryController
import com.soyle.stories.characterarc.deleteCharacterDialog.DeleteCharacterDialogController
import com.soyle.stories.characterarc.deleteCharacterDialog.DeleteCharacterDialogState
import com.soyle.stories.characterarc.deleteCharacterDialog.DeleteCharacterDialogView
import com.soyle.stories.characterarc.deleteCharacterDialog.DeleteCharacterDialogViewListener
import com.soyle.stories.common.onChangeUntil
import com.soyle.stories.di.get
import com.soyle.stories.di.resolve
import com.soyle.stories.domain.character.Character
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.usecase.character.removeCharacterFromStory.RemoveCharacterFromStory
import com.soyle.stories.usecase.theme.addSymbolicItemToOpposition.CharacterId
import javafx.scene.Parent
import javafx.scene.control.Alert
import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType
import javafx.stage.Modality
import javafx.stage.StageStyle
import tornadofx.*

class DeleteCharacterForm : Fragment() {

    class InDialog(private val projectScope: ProjectScope) : DeleteCharacterFlow
    {

        override fun start(characterId: Character.Id, characterName: String) {
            // TODO: check if delete character dialog should open

            setInScope(DeleteCharacterFormViewModel(
                characterId,
                characterName
            ), projectScope, DeleteCharacterFormViewModel::class)

            projectScope.get<DeleteCharacterForm>()
                .openModal(StageStyle.DECORATED, Modality.APPLICATION_MODAL)
        }
    }

    override val scope: ProjectScope = super.scope as ProjectScope

    private val model = resolve<DeleteCharacterFormViewModel>()

    private val alert = Alert(Alert.AlertType.CONFIRMATION)

    override val root: Parent = alert.dialogPane.apply {
        headerTextProperty().bind(model.message)
        content = vbox {
            /*checkbox {
                textProperty().bind(model.doNotShowLabel)
                selectedProperty().bindBidirectional(model.doDefaultAction)
            }*/
        }
        buttonTypes.setAll(
            ButtonType(model.deleteButtonLabel.value, Delete),
            ButtonType(model.cancelButtonLabel.value, Cancel)
        )
    }

    private fun deleteCharacter() {
        val deleteCharacterController = scope.get<RemoveCharacterFromStoryController>()
        deleteCharacterController.confirmRemoveCharacter(model.characterId)
            .invokeOnCompletion {
                if (it == null) runLater { close() }
            }
    }

    init {
        titleProperty.bind(model.title)

        alert.resultProperty().onChangeOnce {
            when (it?.buttonData) {
                Delete -> deleteCharacter()
                else -> close()
            }
        }
    }

    companion object {
        private inline val Delete get() = ButtonBar.ButtonData.FINISH
        private inline val Cancel get() = ButtonBar.ButtonData.CANCEL_CLOSE
    }
}