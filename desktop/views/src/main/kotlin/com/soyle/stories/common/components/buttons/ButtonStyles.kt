package com.soyle.stories.common.components.buttons

import com.soyle.stories.common.ColorStyles
import com.soyle.stories.common.components.ComponentsStyles
import com.soyle.stories.soylestories.Styles
import com.soyle.stories.soylestories.Styles.Companion.Blue
import com.soyle.stories.soylestories.Styles.Companion.Purple
import com.soyle.stories.soylestories.Styles.Companion.secondaryColor
import com.soyle.stories.soylestories.modena
import javafx.scene.Cursor
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import tornadofx.*

class ButtonStyles : Stylesheet() {
    companion object {
        val inviteButton by cssclass()

        /**
         * Typically used for a [MenuButton] to remove the arrow.
         */
        val noArrow by cssclass()

        init {
            importStylesheet<ButtonStyles>()
        }
    }
    init {
        val modenaWithPrimaryBase = modena(secondaryColor)
        val modenaWithSecondaryBase = modena(ColorStyles.primaryColor)
        val standardButton = mixin {
            padding = box(8.px, 6.px)
            fontSize = 1.0.em
        }

        button {
            backgroundRadius = multi(box(4.px))
            and(ComponentsStyles.primary, ComponentsStyles.secondary) {
                fontSize = 1.1.em
                fontWeight = FontWeight.BOLD
                padding = box(6.px, 8.px)
                cursor = Cursor.HAND
                baseColor = Color.WHITE
                backgroundInsets = multi(box(0.px))
                backgroundColor = multi(Color.TRANSPARENT)
            }
            and(ComponentsStyles.filled) {
                +standardButton
            }
            and(ComponentsStyles.outlined) {
                +standardButton
                padding = box(3.px, 7.px)
                backgroundColor = multi(Color.TRANSPARENT)
                borderWidth = multi(box(2.px))
                borderRadius = multi(box(4.px))
            }
            and(ComponentsStyles.primary) {
                textFill = secondaryColor
                and(hover) {
                    backgroundColor = multi(secondaryColor.deriveColor(1.0, 1.0, 1.0, 0.1))
                }
                and(ComponentsStyles.outlined) {
                    borderColor = multi(box(secondaryColor))
                }
                and(ComponentsStyles.filled) {
                    textFill = Color.WHITE
                    backgroundColor = multi(
                        modenaWithPrimaryBase.shadowHighlightColor,
                        modenaWithPrimaryBase.outerBorder,
                        modenaWithPrimaryBase.innerBorder,
                        modenaWithPrimaryBase.bodyColor
                    )
                    and(hover) {
                        backgroundColor = multi(modenaWithPrimaryBase.hoverBase)
                    }
                }
            }
            /*
            use primary color in secondary buttons because it will blend in more with the rest of the design and thus
            be less visible.  We use the secondary color in the primary buttons because it's more contrasting.
             */
            and(ComponentsStyles.secondary) {
                textFill = ColorStyles.primaryColor
                and(hover) {
                    backgroundColor = multi(ColorStyles.primaryColor.deriveColor(1.0, 1.0, 1.0, 0.1))
                }
                and(ComponentsStyles.outlined) {
                    borderColor = multi(box(ColorStyles.primaryColor))
                }
                and(ComponentsStyles.filled) {
                    textFill = Color.WHITE
                    backgroundColor = multi(
                        modenaWithSecondaryBase.shadowHighlightColor,
                        modenaWithSecondaryBase.outerBorder,
                        modenaWithSecondaryBase.innerBorder,
                        modenaWithSecondaryBase.bodyColor
                    )
                    and(hover) {
                        backgroundColor = multi(modenaWithSecondaryBase.hoverBase)
                    }
                }
            }
            and(inviteButton) {
                padding = box(8.px, 16.px)
            }
        }
        menuButton and ComponentsStyles.primary {
            baseColor = Color.WHITE
            fontWeight = FontWeight.BOLD
            +standardButton
            label {
                padding = box(0.px)
            }

            and(ComponentsStyles.filled) {
                baseColor = secondaryColor
                arrow {
                    backgroundColor = multi(Color.WHITE)
                }
            }
            and(ComponentsStyles.outlined) {
                baseColor = Color.WHITE
                borderWidth = multi(box(2.px))
                borderRadius = multi(box(4.px))
                padding = box(7.px, 6.px)
                borderColor = multi(box(secondaryColor))
                label {
                    fill = secondaryColor
                    textFill = secondaryColor
                }
                arrow {
                    backgroundColor = multi(secondaryColor)
                }
            }
        }
        menuButton and ComponentsStyles.secondary {
            fontWeight = FontWeight.BOLD
            +standardButton
            label {
                padding = box(0.px)
            }

            and(ComponentsStyles.filled) {
                baseColor = ColorStyles.primaryColor
                textFill = Color.WHITE
                arrow {
                    backgroundColor = multi(Color.WHITE)
                }
            }
            and(ComponentsStyles.outlined) {
                baseColor = Color.WHITE
                label {
                    fill = ColorStyles.primaryColor
                    textFill = ColorStyles.primaryColor
                }
                borderWidth = multi(box(2.px))
                borderRadius = multi(box(4.px))
                padding = box(7.px, 6.px)
                borderColor = multi(box(ColorStyles.primaryColor))
                arrow {
                    backgroundColor = multi(ColorStyles.primaryColor)
                }
            }
        }
        contextMenu {
            baseColor = Color.WHITE
            fontWeight = FontWeight.NORMAL
            menuItem {
                label {
                    fill = Color.BLACK
                    textFill = Color.BLACK
                }
                and(hover, armed, selected, focused) {
                    label {
                        fill = Color.WHITE
                        textFill = Color.WHITE
                    }
                }
            }
        }

        inviteButton {
            padding = box(8.px, 16.px)
        }

        menuButton and noArrow {
            arrowButton {
                padding = box(0.px)
                arrow {
                    padding = box(0.px)
                }
            }
        }
    }
}