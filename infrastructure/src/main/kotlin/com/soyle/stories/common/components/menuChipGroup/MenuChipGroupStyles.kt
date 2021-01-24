package com.soyle.stories.common.components.menuChipGroup

import javafx.scene.paint.Color
import tornadofx.*

class MenuChipGroupStyles : Stylesheet() {

    companion object {

        val menuChipGroup by cssclass()
        val showing by csspseudoclass()

        val arrow by cssclass()
        val arrowButton by cssclass()

        init {
            importStylesheet<MenuChipGroupStyles>()
        }

    }

    init {
        menuChipGroup {
            unsafe("-fx-text-fill", raw("-fx-text-inner-color"))
            unsafe("-fx-highlight-fill", raw("derive(-fx-control-inner-background,-20%)"))
            unsafe("-fx-highlight-text-fill", raw("-fx-text-inner-color"))
            unsafe("-fx-prompt-text-fill", raw("derive(-fx-control-inner-background,-30%)"))
            unsafe("-fx-background-color", raw("linear-gradient(to bottom, derive(-fx-text-box-border, -10%), -fx-text-box-border),\n" +
                    "        linear-gradient(from 0px 0px to 0px 5px, derive(-fx-control-inner-background, -9%), -fx-control-inner-background)"))
            unsafe("-fx-background-insets", raw("0, 1"))
            unsafe("-fx-background-radius", raw("3, 2"))
            unsafe("-fx-padding", raw("0.333333em 0.583em 0.333333em 0.583em"))

            and(focused) {
                unsafe("-fx-highlight-fill", raw("-fx-accent"))
                unsafe("-fx-highlight-text-fill", raw("white"))
                unsafe("-fx-background-color", raw("-fx-focus-color,\n" +
                        "        -fx-control-inner-background,\n" +
                        "        -fx-faint-focus-color,\n" +
                        "        linear-gradient(from 0px 0px to 0px 5px, derive(-fx-control-inner-background, -9%), -fx-control-inner-background)"))
                unsafe("-fx-background-insets", raw("-0.2, 1, -1.4, 3"))
                unsafe("-fx-background-radius", raw("3, 2, 4, 0"))
            }

            and(disabled) {
                opacity = 0.4
            }

            arrowButton {
                padding = box(0.5.em, 0.667.em, 0.5.em, 0.0.em)

                arrow {
                    unsafe("-fx-background-color", raw("-fx-mark-highlight-color, -fx-mark-color"))
                    unsafe("-fx-background-insets", raw("0 0 -1 0, 0"))
                    padding = box(0.166667.em, 0.333333.em, 0.166667.em, 0.333333.em)
                    unsafe("-fx-shape", raw("\"M 0 0 h 7 l -3.5 4 z\""))

                }
            }
        }
    }


}