package com.soyle.stories.theme.characterConflict

import com.soyle.stories.common.components.PopOutEditBox
import com.soyle.stories.common.components.editableText
import com.soyle.stories.common.components.popOutEditBox
import com.soyle.stories.di.resolve
import javafx.geometry.Orientation
import javafx.scene.Parent
import tornadofx.*

class CharacterConflict : View() {

    private val viewListener = resolve<CharacterConflictViewListener>()
    private val model = resolve<CharacterConflictModel>()

    override val root: Parent = form {
        vbox {
            hbox {
                fieldset(labelPosition = Orientation.VERTICAL) {
                    field {
                        textProperty.bind(model.centralConflictFieldLabel)
                        textfield(model.centralConflict) {
                            popOutEditBox = PopOutEditBox(this, textProperty())
                            focusedProperty().onChange {
                                if (it) popOutEditBox?.popup()
                            }
                        }
                    }
                }
            }
        }
    }

    init {
        model.invalidatedProperty().onChange {
            getValidStateIfInvalid(it)
        }
        getValidStateIfInvalid(model.invalidated)
    }

    private fun getValidStateIfInvalid(invalidated: Boolean) {
        if (invalidated) viewListener.getValidState()
    }

}