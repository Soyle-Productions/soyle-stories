package com.soyle.stories.common.components

import javafx.scene.paint.Color
import tornadofx.*

class ComponentsStyles : Stylesheet() {
    companion object {
        val card by cssclass()
        val cardHeader by cssclass()
        val cardBody by cssclass()

        init {
            importStylesheet<ComponentsStyles>()
        }
    }

    init {
        card {
            backgroundColor += Color.WHITE
            backgroundRadius += box(4.px)
            borderRadius += box(4.px)
            borderColor += box(Color.rgb(0,0,0,0.12))
            borderWidth += box(1.px)
            minWidth = 275.px
        }
        cardHeader {
            padding = box(16.px)
        }
        cardBody {
            padding = box(16.px)
        }
    }
}