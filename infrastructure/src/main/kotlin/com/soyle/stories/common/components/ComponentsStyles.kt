package com.soyle.stories.common.components

import com.soyle.stories.soylestories.Styles
import javafx.scene.effect.BlurType
import javafx.scene.effect.DropShadow
import javafx.scene.paint.Color
import tornadofx.*

class ComponentsStyles : Stylesheet() {
    companion object {

        const val baseSpace = 8.0

        val card by cssclass()
        val liftedCard by cssclass()
        val cardHeader by cssclass()
        val cardBody by cssclass()
        val firstChild by csspseudoclass("first-child")
        val notFirstChild by csspseudoclass("not-first-child")

        val buttonCombo by cssclass()
        val arrowIconButton by cssclass()
        val iconButton by cssclass()

        val editableText by cssclass(value = EditableText.DEFAULT_STYLE_CLASS)

        val glyphIcon by cssclass("glyph-icon")
        val noDisableStyle by cssclass()
        val noSelectionMenuItem by cssclass()
        val contextMenuSectionHeaderItem by cssclass()
        val contextMenuSectionedItem by cssclass()
        val discouragedSelection by cssclass()

        val hasProblem by cssclass()

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
            and(notFirstChild) {
                padding = box(0.px, 16.px, 16.px, 16.px)
            }
        }
        cardBody {
            padding = box(16.px)
            and(notFirstChild) {
                padding = box(0.px, 16.px, 16.px, 16.px)
            }
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

        editableText {
            underline = false
            glyphIcon {
                visibility = FXVisibility.HIDDEN
            }
            and(focused) {
                glyphIcon {
                    visibility = FXVisibility.VISIBLE
                }
            }
            and(hover) {
                glyphIcon {
                    visibility = FXVisibility.VISIBLE
                }
            }
        }

        noDisableStyle {
            and(disabled) {
                opacity = 1.0
            }
        }

        noSelectionMenuItem {
            and(hover) {
                label {
                    textFill = Color.BLACK
                }
                backgroundColor = multi(Color.TRANSPARENT)
            }
            and(focused) {
                label {
                    textFill = Color.BLACK
                }
                backgroundColor = multi(Color.TRANSPARENT)
            }
        }
        contextMenuSectionHeaderItem {
            label {
                fontSize = 1.2.em
                textFill = Color.DARKGRAY
            }
            and(disabled) {
                opacity = 1.0
            }
            and(hover) {
                label {
                    textFill = Color.DARKGRAY
                }
            }
            and(focused) {
                label {
                    textFill = Color.DARKGRAY
                }
            }
        }
        contextMenuSectionedItem {
            label {
                translateX = 8.px
            }
        }
        discouragedSelection {
            label {
                textFill = Color.DARKGRAY
            }
        }
        popup {
            backgroundColor += Color.WHITE
            borderColor += box(Styles.Purple)
            borderWidth += box(1.px)
        }
        hasProblem {
            borderWidth += box(0.px, 0.px, 2.px, 0.px)
            borderColor += box(Color.TRANSPARENT, Color.TRANSPARENT, Styles.Orange, Color.TRANSPARENT)
        }
    }
}