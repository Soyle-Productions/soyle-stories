package com.soyle.stories.theme.deleteSymbolDialog

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

class DeleteSymbolDialog : Fragment() {

    override val scope: ProjectScope = super.scope as ProjectScope

    val model = resolve<DeleteSymbolDialogModel>()

    private val alert = Alert(Alert.AlertType.CONFIRMATION)

    override val root: Parent = alert.dialogPane.apply {
        headerTextProperty().bind(model.message)
        content = vbox {
            checkbox {
                textProperty().bind(model.doNotShowLabel)
                selectedProperty().bindBidirectional(model.doDefaultAction)
            }
        }
        model.itemProperty.onChange { viewModel ->
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
        model.itemProperty.onChangeUntil({ it?.doDefaultAction != null }) {
            if (it?.doDefaultAction == false) {
                openModal(StageStyle.DECORATED, Modality.APPLICATION_MODAL)
            } else if (it?.doDefaultAction == true) {
                alert.result = ButtonType("", Delete)
            }
        }
    }

    private var internalScope: DeleteSymbolDialogScope? = null

    override fun onUndock() {
        internalScope?.close()
    }

    fun show(symbolId: String, symbolName: String) {
        val internalScope = DeleteSymbolDialogScope(scope, symbolId, symbolName)
        val viewListener = resolve<DeleteSymbolDialogViewListener>(internalScope)
        alert.resultProperty().onChangeOnce {
            when (it?.buttonData) {
                Delete -> viewListener.deleteSymbol(! model.doDefaultAction.value)
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