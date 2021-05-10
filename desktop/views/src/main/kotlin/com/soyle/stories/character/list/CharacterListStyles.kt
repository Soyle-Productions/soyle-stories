package com.soyle.stories.character.list

import com.soyle.stories.common.ColorStyles
import com.soyle.stories.common.components.text.TextStyles
import com.soyle.stories.common.components.text.TextStyles.Companion.sectionTitle
import javafx.scene.paint.Color
import tornadofx.*
import tornadofx.Stylesheet.Companion.box

class CharacterListStyles : Stylesheet() {
    companion object {
        val characterCard by cssclass()

        init {
            importStylesheet<CharacterListStyles>()
        }
    }

    init {
        characterCard {
            and(hover) {
                padding = box(1.px)
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
            }
        }
    }
}