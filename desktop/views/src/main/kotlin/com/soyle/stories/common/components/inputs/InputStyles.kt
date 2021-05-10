package com.soyle.stories.common.components.inputs

import javafx.scene.paint.Color
import tornadofx.Stylesheet
import tornadofx.importStylesheet

class InputStyles : Stylesheet() {

    companion object {

        init {
            importStylesheet<InputStyles>()
        }
    }

    init {
        textField {
            baseColor = Color.WHITE
        }
    }

}