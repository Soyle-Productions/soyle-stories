package com.soyle.stories.characterarc

import com.soyle.stories.common.ColorStyles
import com.soyle.stories.common.getValue
import de.jensd.fx.glyphs.materialicons.MaterialIcon
import javafx.scene.paint.Color
import tornadofx.*

class CharacterArcStyles : Stylesheet() {

    companion object {
        val defaultCharacterImage = MaterialIcon.PERM_IDENTITY

        val characterIcon by cssclass()

        init {
            importStylesheet<CharacterArcStyles>()
        }
    }

    init {
        characterIcon {
            backgroundColor = multi(Color.web("#E5E5E5"))
            backgroundRadius = multi(box(50.percent))
            borderRadius = multi(box(50.percent))
            borderWidth = multi(box(2.px))
            borderColor = multi(box(ColorStyles.primaryColor))
        }
    }

}