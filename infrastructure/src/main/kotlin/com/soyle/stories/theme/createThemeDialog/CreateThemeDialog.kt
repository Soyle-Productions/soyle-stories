package com.soyle.stories.theme.createThemeDialog

import com.soyle.stories.common.onChangeUntil
import com.soyle.stories.di.resolve
import javafx.scene.Parent
import javafx.stage.Modality
import javafx.stage.StageStyle
import javafx.stage.Window
import tornadofx.*

class CreateThemeDialog : Fragment() {

    private val viewListener = resolve<CreateThemeDialogViewListener>()
    private val model = resolve<CreateThemeDialogModel>()

    override val root: Parent = form {
        fieldset {
            field {
                textProperty.bind(model.nameFieldLabel)
                textfield {
                    model.errorMessage.onChange {
                        decorators.toList().forEach { removeDecorator(it) }
                        if (it != null) addDecorator(SimpleMessageDecorator(it, ValidationSeverity.Error))
                    }
                    action {
                        viewListener.createTheme(text)
                    }
                }
            }
        }
    }

    init {
        titleProperty.bind(model.title)
        viewListener.getValidState()
    }

    fun show(parentWindow: Window? = null)
    {
        if (currentStage?.isShowing == true) return
        openModal(StageStyle.DECORATED, Modality.APPLICATION_MODAL, escapeClosesWindow = true, owner = parentWindow)
        model.created.onChangeUntil({ it == true || currentStage?.isShowing != true }) {
            if (it == true) close()
        }
    }
}