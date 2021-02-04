package com.soyle.stories.theme.deleteValueWebDialog

import com.soyle.stories.common.onChangeUntil
import com.soyle.stories.di.get
import com.soyle.stories.di.resolve
import com.soyle.stories.project.ProjectScope
import javafx.scene.Parent
import javafx.scene.control.Alert
import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType
import javafx.stage.Modality
import javafx.stage.StageStyle
import tornadofx.*

class DeleteValueWebDialog : Fragment() {

    override val scope: ProjectScope = super.scope as ProjectScope

    val model = resolve<DeleteValueWebDialogModel>()

    private val alert = Alert(Alert.AlertType.CONFIRMATION)

    override val root: Parent = alert.dialogPane.apply {
        headerTextProperty().bind(model.message)
        content = vbox {
            checkbox {
                textProperty().bind(model.doNotShowLabel)
                selectedProperty().bindBidirectional(model.doDefaultAction)
            }
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

    private var internalScope: DeleteValueWebDialogScope? = null

    override fun onUndock() {
        internalScope?.close()
    }

    fun show(valueWebId: String, valueWebName: String) {
        val internalScope = DeleteValueWebDialogScope(scope, valueWebId, valueWebName)
        val viewListener: DeleteValueWebDialogViewListener = internalScope.get()
        alert.resultProperty().onChangeOnce {
            when (it?.buttonData) {
                Delete -> viewListener.deleteValueWeb(! model.doDefaultAction.value)
                else -> {}
            }
            internalScope.close()
            close()
        }
        this.internalScope = internalScope
        viewListener.getValidState()
    }

    companion object {
        private inline val Delete get() = ButtonBar.ButtonData.FINISH
        private inline val Cancel get() = ButtonBar.ButtonData.CANCEL_CLOSE
    }
}