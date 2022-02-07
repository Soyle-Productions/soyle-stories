package com.soyle.stories.common.components.inputs

import com.soyle.stories.common.styleImporter
import javafx.scene.paint.Color
import tornadofx.*

class InputStyles : Stylesheet() {

    companion object {

        val nonBlankTextField by cssclass()
        val numberField by cssclass()

        init {
            styleImporter<InputStyles>()
        }
    }

    init {
        textField {
//            baseColor = Color.WHITE
            padding = box(8.px)
        }
    }

}