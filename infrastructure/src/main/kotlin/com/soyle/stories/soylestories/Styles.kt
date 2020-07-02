package com.soyle.stories.soylestories

import javafx.scene.paint.Color
import tornadofx.Stylesheet

class Styles : Stylesheet() {

    companion object {

        val Purple = Color.web("#862F89")
        val Orange = Color.web("#D38147")
        val Blue = Color.web("#3A518E")

    }

    init {
        root {
            accentColor = Blue
            focusColor = Purple
        }
    }

}