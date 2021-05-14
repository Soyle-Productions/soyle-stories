package com.soyle.stories.character.list

import com.soyle.stories.common.ColorStyles
import com.soyle.stories.common.components.ComponentsStyles.Companion.cardBody
import com.soyle.stories.common.components.ComponentsStyles.Companion.cardHeader
import com.soyle.stories.common.components.text.TextStyles
import com.soyle.stories.common.components.text.TextStyles.Companion.sectionTitle
import javafx.scene.paint.Color
import tornadofx.*
import tornadofx.Stylesheet.Companion.box

class CharacterListStyles : Stylesheet() {
    companion object {
        val characterCard by cssclass()
        val characterArc by cssclass()

        init {
            importStylesheet<CharacterListStyles>()
        }
    }

    init {
        characterCard {
            arrowButton {
                padding = box(0.5.em, 0.25.em, 0.5.em, 0.0.em)
            }
            arrow {
                backgroundColor = multi(Color.BLACK)
                backgroundInsets = multi(box(0.px, 0.px, (-1).px, 0.px), box(0.px))
                padding = box(0.25.em)
                shape = "M 0 0 v 7 l 4 -3.5 z"
                rotate = 90.deg
            }

            and(expanded) {
                arrow {
                    rotate = 270.deg
                }
            }

            cardHeader {
                and(hover) {
                    sectionTitle {
                        textFill = Color.BLACK
                    }
                    backgroundColor = multi(ColorStyles.lightHighlightColor)
                }
                and(selected) {
                    sectionTitle {
                        textFill = ColorStyles.lightSelectionTextColor
                    }
                    backgroundColor = multi(ColorStyles.lightSelectionColor)
                    arrow {
                        backgroundColor = multi(ColorStyles.lightSelectionTextColor)
                    }
                }
            }
            cardBody {
                padding = box(8.px, 16.px)
            }

            characterArc {
                padding = box(4.px)
                and(hover) {
                    textFill = Color.BLACK
                    backgroundColor = multi(ColorStyles.lightHighlightColor)
                }
                and(selected) {
                    textFill = ColorStyles.lightSelectionTextColor
                    backgroundColor = multi(ColorStyles.lightSelectionColor)
                }
            }
        }
    }
}