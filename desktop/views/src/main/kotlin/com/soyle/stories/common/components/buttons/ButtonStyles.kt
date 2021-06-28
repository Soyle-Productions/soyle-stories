package com.soyle.stories.common.components.buttons

import com.soyle.stories.common.components.ComponentsStyles
import com.soyle.stories.soylestories.Styles.Companion.Blue
import com.soyle.stories.soylestories.Styles.Companion.Purple
import com.soyle.stories.soylestories.modena
import javafx.scene.Cursor
import javafx.scene.paint.Color
import javafx.scene.paint.Stop
import javafx.scene.text.FontWeight
import tornadofx.*

class ButtonStyles : Stylesheet() {
    companion object {
        val inviteButton by cssclass()

        init {
            importStylesheet<ButtonStyles>()
        }
    }
    init {
        val modenaPurple = modena(Purple)
        val modenaBlue = modena(Blue)
        val standardButton = mixin {
            padding = box(0.333333.em, 0.666667.em)
            fontSize = 1.0.em
        }

        button {
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
                textFill = Purple
                and(hover) {
                    backgroundColor = multi(Purple.deriveColor(1.0, 1.0, 1.0, 0.1))
                }
                and(ComponentsStyles.outlined) {
                    borderColor = multi(box(Purple))
                }
                and(ComponentsStyles.filled) {
                    textFill = Color.WHITE
                    backgroundColor = multi(
                        modenaPurple.shadowHighlightColor,
                        modenaPurple.outerBorder,
                        modenaPurple.innerBorder,
                        modenaPurple.bodyColor
                    )
                    and(hover) {
                        backgroundColor = multi(modenaPurple.hoverBase)
                    }
                }
            }
            and(ComponentsStyles.secondary) {
                textFill = Blue
                and(hover) {
                    backgroundColor = multi(Blue.deriveColor(1.0, 1.0, 1.0, 0.1))
                }
                and(ComponentsStyles.outlined) {
                    borderColor = multi(box(Blue))
                }
                and(ComponentsStyles.filled) {
                    textFill = Color.WHITE
                    backgroundColor = multi(
                        modenaBlue.shadowHighlightColor,
                        modenaBlue.outerBorder,
                        modenaBlue.innerBorder,
                        modenaBlue.bodyColor
                    )
                    and(hover) {
                        backgroundColor = multi(modenaBlue.hoverBase)
                    }
                }
            }
        }
        menuButton and ComponentsStyles.primary {
            baseColor = Color.WHITE
            fontWeight = FontWeight.BOLD

            and(ComponentsStyles.filled) {
                baseColor = Purple
                arrow {
                    backgroundColor = multi(Color.WHITE)
                }
            }
            and(ComponentsStyles.outlined) {
                baseColor = Color.WHITE
                borderWidth = multi(box(2.px))
                borderRadius = multi(box(4.px))
                borderColor = multi(box(Purple))
                label {
                    fill = Purple
                    textFill = Purple
                }
                arrow {
                    backgroundColor = multi(Purple)
                }
            }
        }
        menuButton and ComponentsStyles.secondary {
            fontWeight = FontWeight.BOLD
            and(ComponentsStyles.filled) {
                baseColor = Blue
                textFill = Color.WHITE
                arrow {
                    backgroundColor = multi(Color.WHITE)
                }
            }
            and(ComponentsStyles.outlined) {
                baseColor = Color.WHITE
                label {
                    fill = Blue
                    textFill = Blue
                }
                borderWidth = multi(box(2.px))
                borderRadius = multi(box(4.px))
                borderColor = multi(box(Blue))
                arrow {
                    backgroundColor = multi(Blue)
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
    }
}