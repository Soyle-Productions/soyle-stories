package com.soyle.stories.theme.createValueWebDialog

import com.soyle.stories.common.onChangeUntil
import com.soyle.stories.di.resolve
import com.soyle.stories.domain.validation.NonBlankString
import javafx.geometry.Orientation
import javafx.scene.Parent
import javafx.stage.Modality
import javafx.stage.StageStyle
import javafx.stage.Window
import tornadofx.*

class CreateValueWebDialog : Fragment() {

    private val viewListener = resolve<CreateValueWebDialogViewListener>()
    private val model = resolve<CreateValueWebDialogModel>()

    private lateinit var themeId: String
    private var characterId: String? = null

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
                        val nonBlankName = NonBlankString.create(text)
                        if (nonBlankName != null) {
                            if (characterId != null) {
                                viewListener.createValueWebAndLinkCharacter(themeId, nonBlankName, characterId!!)
                            } else {
                                viewListener.createValueWeb(themeId, nonBlankName)
                            }
                        } else {
                            model.errorMessage.value = "Name cannot be blank"
                        }
                    }
                }
            }
        }
    }

    init {
        titleProperty.bind(model.title)
    }

    fun show(themeId: String, parentWindow: Window? = null) {
        if (currentStage?.isShowing == true) return
        this.themeId = themeId
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
    fun showToAutoLinkCharacter(themeId: String, characterId: String? = null, parentWindow: Window? = null)
    {
        if (currentStage?.isShowing == true) return
        this.characterId = characterId
        show(themeId, parentWindow)
    }

    companion object {
        private var minimumWindowWidth: Double? = null
    }
}