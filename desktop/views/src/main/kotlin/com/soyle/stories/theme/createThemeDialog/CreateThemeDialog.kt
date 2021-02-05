package com.soyle.stories.theme.createThemeDialog

import com.soyle.stories.common.onChangeUntil
import com.soyle.stories.di.resolve
import javafx.geometry.Orientation
import javafx.scene.Parent
import javafx.stage.Modality
import javafx.stage.StageStyle
import javafx.stage.Window
import tornadofx.*
import kotlin.properties.Delegates

class CreateThemeDialog : Fragment() {

    private val viewListener = resolve<CreateThemeDialogViewListener>()
    private val model = resolve<CreateThemeDialogModel>()

    override val root: Parent = form {
        fieldset(labelPosition = Orientation.VERTICAL) {
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
    }

    fun show(parentWindow: Window? = null)
    {
        if (currentStage?.isShowing == true) return
        openModal(StageStyle.DECORATED, Modality.APPLICATION_MODAL, escapeClosesWindow = true, owner = parentWindow)?.apply {
            if (minimumWindowWidth == null) {
                model.title.onChangeOnce {
                    val text = root.text(it)
                    minimumWindowWidth = text.layoutBounds.width + width
                    text.removeFromParent()
                    minWidth = minimumWindowWidth!!
                }
            } else {
                minWidth = minimumWindowWidth!!
            }
        }
        model.created.onChangeUntil({ it == true || currentStage?.isShowing != true }) {
            if (it == true) close()
        }
        viewListener.getValidState()
    }

    companion object {
        private var minimumWindowWidth: Double? = null
    }
}