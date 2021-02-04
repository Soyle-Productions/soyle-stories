package com.soyle.stories.theme.createOppositionValueDialog

import com.soyle.stories.common.onChangeUntil
import com.soyle.stories.di.resolve
import javafx.geometry.Orientation
import javafx.scene.Parent
import javafx.stage.Modality
import javafx.stage.StageStyle
import javafx.stage.Window
import tornadofx.*

class CreateOppositionValueDialog : Fragment() {

    private val viewListener = resolve<CreateOppositionValueDialogViewListener>()
    private val model = resolve<CreateOppositionValueDialogModel>()

    private var valueWebId by singleAssign<String>()
    private var characterId by singleAssign<String>()

    init {
        titleProperty.bind(model.title)
    }

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
                        viewListener.createOppositionValue(valueWebId, text, characterId)
                    }
                }
            }
        }
    }

    private fun show(valueWebId: String, parentWindow: Window? = null) {
        if (currentStage?.isShowing == true) return
        this.valueWebId = valueWebId
        openModal(
            StageStyle.DECORATED,
            Modality.APPLICATION_MODAL,
            escapeClosesWindow = true,
            owner = parentWindow
        )?.apply {
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
    fun showToAutoLinkCharacter(valueWebId: String, characterId: String, parentWindow: Window? = null) {
        if (currentStage?.isShowing == true) return
        this.characterId = characterId
        show(valueWebId, parentWindow)
    }

    companion object {
        private var minimumWindowWidth: Double? = null
    }
}