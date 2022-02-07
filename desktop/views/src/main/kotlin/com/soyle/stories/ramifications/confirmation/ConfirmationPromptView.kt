package com.soyle.stories.ramifications.confirmation

import com.soyle.stories.di.resolve
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.Alert
import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType
import javafx.scene.control.DialogPane
import javafx.stage.Stage
import javafx.stage.Window
import tornadofx.*

typealias DefaultConfirmationPromptView = ConfirmationPromptView<ConfirmationPromptViewModel>
class ConfirmationPromptView<ViewModel : ConfirmationPromptViewModel> : Fragment() {

    private val locale: ConfirmationPromptLocale = resolve()

    private val _confirmationText = stringProperty()
    init { _confirmationText.bind(locale.confirm) }
    fun confirmationText() = _confirmationText
    var confirmationText: String by _confirmationText

    private val _headerText = stringProperty()
    fun headerText() = _headerText
    var headerText: String by _headerText

    private val _content = objectProperty<Node?>()
    fun content() = _content
    var content: Node? by _content

    val viewModel: ViewModel by params

    private val alert = Alert(Alert.AlertType.CONFIRMATION)

    override val root: Parent = alert.dialogPane.apply {
        headerTextProperty().bind(headerText())
        content = vbox {
            dynamicContent(content()) {
                if (it != null) add(it)
                checkbox {
                    textProperty().bind(locale.doNotShowDialogAgain)
                    selectedProperty().bindBidirectional(viewModel.doNotShowAgain())
                }
            }
        }
        setButtonTypes()
        confirmationText().onChange { setButtonTypes() }
        viewModel.onCheck().onChange { setButtonTypes() }
    }

    private fun DialogPane.setButtonTypes() {
        buttonTypes.setAll(
            listOfNotNull(
                ButtonType(confirmationText, ButtonBar.ButtonData.FINISH),
                ButtonType(locale.ramifications.value, ButtonBar.ButtonData.OTHER).takeIf { viewModel.onCheck != null },
                ButtonType(locale.cancel.value, ButtonBar.ButtonData.CANCEL_CLOSE)
            )
        )
    }

    init {
        alert.resultProperty().onChange {
            when (it?.buttonData) {
                ButtonBar.ButtonData.FINISH -> viewModel.confirm()
                ButtonBar.ButtonData.OTHER -> viewModel.check()
                else -> viewModel.cancel()
            }
        }
    }
}

fun defaultConfirmationPrompt(
    scope: Scope = FX.defaultScope,
    ownerWindow: Stage? = null,
    viewModel: ConfirmationPromptViewModel = ConfirmationPromptViewModel(),
    configure: DefaultConfirmationPromptView.() -> Unit = {}
): ConfirmationPromptViewModel {
    val view = find<DefaultConfirmationPromptView>(scope, DefaultConfirmationPromptView::viewModel to viewModel)
    view.configure()
    viewModel.isNeeded().onChange {
        if (it) {
            view.openModal(owner = ownerWindow)?.apply {
                setOnHidden { view.viewModel.cancel() }
            }
        }
        else view.close()
    }
    return viewModel
}

fun <ViewModel : ConfirmationPromptViewModel> confirmationPrompt(
    scope: Scope = FX.defaultScope,
    ownerWindow: Window? = null,
    viewModel: ViewModel,
    configure: ConfirmationPromptView<ViewModel>.() -> Unit = {}
): ViewModel {
    val view = find<ConfirmationPromptView<ViewModel>>(scope, ConfirmationPromptView<ViewModel>::viewModel to viewModel)
    view.configure()
    viewModel.isNeeded().onChange {
        if (it) {
            view.openModal(owner = ownerWindow)?.apply {
                setOnHidden { view.viewModel.cancel() }
            }
        }
        else view.close()
    }
    return viewModel
}