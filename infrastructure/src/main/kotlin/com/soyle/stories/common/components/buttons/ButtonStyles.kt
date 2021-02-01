package com.soyle.stories.common.components.buttons

import com.soyle.stories.soylestories.Styles.Companion.Purple
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import tornadofx.*

class ButtonStyles : Stylesheet() {
    companion object {
        val primaryButton by cssclass()
        val inviteButton by cssclass()

        init {
            importStylesheet<ButtonStyles>()
        }
    }
    init {
        primaryButton {
            baseColor = Purple
            textFill = Color.WHITE
            fontWeight = FontWeight.BOLD
        }
        primaryButton and menuButton {
            arrow {
                backgroundColor = multi(Color.WHITE)
            }
            popup {
                backgroundColor = multi(Color.WHITE)
            }
        }
        inviteButton {
            padding = box(8.px, 16.px)
        }
    }
}