package com.soyle.stories.common

import javafx.scene.paint.Color
import tornadofx.Stylesheet

class ColorStyles : Stylesheet() {

    interface ColorFamily {
        val main: Color
        val light: Color
        val dark: Color

        val state: ColorState
    }

    interface ColorState {
        val hover: Color
        val selectedBackground: Color
    }

    companion object {

        val primary: ColorFamily = object: ColorFamily {
            override val main = Color.web("#60408B")
            override val light = Color.web("#AF9FC4")
            override val dark = Color.web("#432D61")

            override val state: ColorState = object: ColorState {
                override val hover = Color.rgb(main.red.toInt(), main.green.toInt(), main.blue.toInt(), 0.5)
                override val selectedBackground = dark
            }
        }

        val primaryColor = Color.web("#60408B")
        val secondaryColor = Color.web("#3F7B88")
        val tertiaryColor = Color.web("#FF4800")

        val lightHighlightColor = Color.web("#9A7CC2")
        val lightSelectionColor = Color.web("#4C326D")
        val lightTextColor = Color.web("#352950")
        val lightTitleTextColor = Color.web("#47376C")
        val lightCaptionTextColor = Color.web("#3F305F")
        val lightSelectionTextColor = Color.web("#EEECEB")

        val Purple = Color.web("#862F89")
        val Orange = Color.web("#D38147")
        val Blue = Color.web("#3A518E")


    }

}