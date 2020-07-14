package com.soyle.stories.common.components

import javafx.scene.effect.BlurType
import javafx.scene.effect.DropShadow
import javafx.scene.paint.Color
import tornadofx.*

class ComponentsStyles : Stylesheet() {
    companion object {
        val card by cssclass()
        val liftedCard by cssclass()
        val cardHeader by cssclass()
        val cardBody by cssclass()

        val buttonCombo by cssclass()
        val arrowIconButton by cssclass()
        val iconButton by cssclass()

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
        liftedCard {
            effect = DropShadow(BlurType.GAUSSIAN, Color.rgb(0,0,0,0.2), 4.0, -2.0, 0.0, 3.0)
        }
        cardHeader {
            padding = box(16.px)
        }
        cardBody {
            padding = box(16.px)
        }

        buttonCombo {
            arrowButton {
                padding = box(0.px)
                arrow {
                    padding = box(0.px)
                    backgroundColor = multi(Color.TRANSPARENT)
                }
            }
        }
        arrowIconButton {
            label {
                padding = box(0.em)
            }
            arrowButton {
                padding = box(0.5.em, 0.667.em, 0.5.em, 0.667.em)
            }
        }
        iconButton {
            focused {
                backgroundColor += Color(0.0, 0.0, 0.0, 0.4)
            }
            hover {
                backgroundColor += Color(0.0, 0.0, 0.0, 0.2)
            }
        }
    }
}