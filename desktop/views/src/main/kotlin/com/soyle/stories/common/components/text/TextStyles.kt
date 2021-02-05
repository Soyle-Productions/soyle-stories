package com.soyle.stories.common.components.text

import javafx.scene.text.FontWeight
import tornadofx.Stylesheet
import tornadofx.cssclass
import tornadofx.importStylesheet
import tornadofx.pt


class TextStyles : Stylesheet() {
    companion object {
        val mainHeader by cssclass()
        init {
            importStylesheet<TextStyles>()
        }
    }

    init {
        mainHeader {
            fontSize = 24.pt
            fontWeight = FontWeight.BOLD
        }
    }

}