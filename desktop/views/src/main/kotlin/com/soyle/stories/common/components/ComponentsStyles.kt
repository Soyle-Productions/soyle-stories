package com.soyle.stories.common.components

import com.soyle.stories.common.ColorStyles
import com.soyle.stories.common.components.buttons.ButtonStyles
import com.soyle.stories.common.components.dataDisplay.list.ListStyles
import com.soyle.stories.common.components.inputs.InputStyles
import com.soyle.stories.common.components.surfaces.SurfaceStyles
import com.soyle.stories.common.components.text.TextStyles
import com.soyle.stories.soylestories.Styles
import javafx.geometry.Pos
import javafx.scene.effect.BlurType
import javafx.scene.effect.DropShadow
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import javafx.scene.text.TextAlignment
import tornadofx.*

class ComponentsStyles : Stylesheet() {
    companion object {

        const val baseSpace = 8.0

        val card by cssclass()
        val lifted = mixin {
            effect = DropShadow(BlurType.GAUSSIAN, Color.rgb(0,0,0,0.2), 4.0, -2.0, 0.0, 3.0)
        }
        val liftedCard by cssclass()
        val cardHeader by cssclass()
        val cardBody by cssclass()
        val firstChild by csspseudoclass("first-child")
        val notFirstChild by csspseudoclass("not-first-child")

        val invitation by cssclass()

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

        val loading by csspseudoclass()
        val loaded by csspseudoclass()

        val primary by csspseudoclass()
        val secondary by csspseudoclass()

        val filled by csspseudoclass()
        val outlined by csspseudoclass()

        init {
            importStylesheet<ComponentsStyles>()

            ButtonStyles // reference to initialize
            TextStyles // reference to initialize
            InputStyles // reference to initialize
            ListStyles // reference to initialize
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
            +lifted
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

        invitation {
            alignment = Pos.CENTER
            spacing = 16.px
            s(TextStyles.fieldLabel) {
                textAlignment = TextAlignment.CENTER
                wrapText = true
                padding = box(16.px)
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
            borderColor += box(ColorStyles.primaryColor)
            borderWidth += box(1.px)
            borderInsets = multi(box((-1).px))
        }
        hasProblem {
            borderWidth += box(0.px, 0.px, 2.px, 0.px)
            borderColor += box(Color.TRANSPARENT, Color.TRANSPARENT, ColorStyles.Orange, Color.TRANSPARENT)
        }

        radioButton {
            baseColor = Color.WHITE
            radio {
                borderColor = multi(box(Color.GREY))
                borderRadius = multi(box(100.percent))
                borderWidth = multi(box(2.px))
            }
        }
        checkBox {
            baseColor = Color.WHITE
            box {
                borderColor = multi(box(Color.GREY))
                borderWidth = multi(box(2.px))
            }
        }

        radioButton and selected {
            radio {
                borderColor = multi(box(ColorStyles.Purple))
            }
            dot {
                backgroundColor = multi(ColorStyles.Purple)
            }
        }

        checkBox and selected {
            box {
                borderColor = multi(box(ColorStyles.Purple))
            }
            mark {
                backgroundColor = multi(ColorStyles.Purple)
            }
        }

        tabPane {
            tab {
                backgroundRadius = multi(box(0.px))
                backgroundColor = multi(SurfaceStyles.lightBackground(5.0))
                labelPadding = box(4.px)
                tabLabel {
                    textFill = ColorStyles.primaryColor
                }
                tabCloseButton {
                    backgroundColor = multi(ColorStyles.primaryColor)
                }
                and(selected) {
                    backgroundColor = multi(ColorStyles.primaryColor)
                    tabLabel {
                        fontWeight = FontWeight.BOLD
                        textFill = Color.WHITE
                    }
                    tabCloseButton {
                        backgroundColor = multi(Color.WHITE)
                    }
                }
            }
            tabHeaderArea {
                padding = box(0.px)
            }
            tabHeaderBackground {
                padding = box(0.px)
                backgroundColor = multi(SurfaceStyles.lightBackground(1.0))
            }
        }
    }
}