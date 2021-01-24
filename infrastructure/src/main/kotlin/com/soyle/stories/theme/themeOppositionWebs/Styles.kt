package com.soyle.stories.theme.themeOppositionWebs

import com.soyle.stories.common.components.ComponentsStyles
import javafx.scene.layout.BorderStrokeStyle
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import tornadofx.*

class Styles : Stylesheet()
{
    companion object {
        val valueWebList by cssclass()
        val selectedItem by cssclass()
        val oppositionCard by cssclass()

        init {
            importStylesheet(Styles::class)
        }
    }
    init {
        valueWebList {
            Stylesheet.hyperlink {
                borderStyle += BorderStrokeStyle.NONE
                borderWidth += box(0.px)
                underline = false
                padding = box(5.px, 5.px, 5.px, 10.px)
            }
            hyperlink and Stylesheet.disabled {
                textFill = Color.BLACK
                opacity = 1.0
            }
        }
        selectedItem {
            fontWeight = FontWeight.BOLD
        }
        oppositionCard {
            ComponentsStyles.cardBody {
                padding = box(0.px, 16.px, 16.px, 16.px)
            }
        }
    }
}