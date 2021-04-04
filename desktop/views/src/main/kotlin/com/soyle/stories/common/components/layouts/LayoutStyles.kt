package com.soyle.stories.common.components.layouts

import javafx.geometry.Pos
import javafx.scene.paint.Color
import tornadofx.*
import tornadofx.Stylesheet.Companion.box

class LayoutStyles : Stylesheet() {
    companion object {
        val emptyToolInvitation by cssclass()
        val primary by csspseudoclass()

        init {
            importStylesheet<LayoutStyles>()
        }
    }
    init {
        emptyToolInvitation {
            alignment = Pos.CENTER
            spacing = 16.px
            padding = box(16.px)
            and(primary) {
                backgroundColor = multi(Color.WHITE)
            }
        }
    }
}