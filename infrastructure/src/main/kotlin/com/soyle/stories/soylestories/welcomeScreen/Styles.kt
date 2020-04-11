package com.soyle.stories.soylestories.welcomeScreen

import javafx.scene.Cursor
import javafx.scene.paint.Color
import tornadofx.*

class WelcomeScreenStyles : Stylesheet() {
    companion object {
        val welcomeButton by cssclass()
        val welcomeButtonGraphic by cssclass()

        init {
			importStylesheet(WelcomeScreenStyles::class)
        }
    }

    init {
        welcomeButton {
            backgroundColor += Color.TRANSPARENT
            textFill = Color.web("#FFC9A3")
            welcomeButtonGraphic {
                fill = Color.web("#FFC9A3")
            }
            graphicTextGap = 7.px
            and(focused) {
                borderWidth += tornadofx.box(1.0.px)
                borderInsets += tornadofx.box((-1.0).px)
                borderColor += box(Color.web("#FFC9A3"))
            }
            and(hover) {
                textFill = Color.WHITE
                cursor = Cursor.HAND
                welcomeButtonGraphic {
                    fill = Color.WHITE
                }
            }
        }
    }
}