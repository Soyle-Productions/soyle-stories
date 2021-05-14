package com.soyle.stories.common.components.text

import com.soyle.stories.common.ColorStyles
import com.soyle.stories.soylestories.Styles
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontSmoothingType
import javafx.scene.text.FontWeight
import tornadofx.*


class TextStyles : Stylesheet() {
    companion object {
        val applicationLevelTitle by cssclass()
        val projectLevelTitle by cssclass()
        val windowLevelTitle by cssclass()
        val toolLevelTitle by cssclass()
        val sectionTitle by cssclass()
        val fieldLabel by cssclass()
        val caption by cssclass()

        val section by cssclass()

        val warning by cssclass()
        val mention by cssclass()

        init {
            importStylesheet<TextStyles>()
        }
    }

    init {
        val title = mixin {
            fontFamily = "Corbel"
            fontSmoothingType = FontSmoothingType.LCD
            textFill = ColorStyles.lightTitleTextColor
        }
        applicationLevelTitle {
            +title
            fontWeight = FontWeight.LIGHT
            fontSize = 63.px
        }
        projectLevelTitle {
            +title
            fontWeight = FontWeight.NORMAL
            fontSize = 50.px
        }
        windowLevelTitle {
            +title
            fontWeight = FontWeight.NORMAL
            fontSize = 36.px
        }
        toolLevelTitle {
            +title
            fontWeight = FontWeight.NORMAL
            fontSize = 25.px
        }
        sectionTitle {
            +title
            fontWeight = FontWeight.MEDIUM
            fontSize = 21.px
        }
        val body = mixin {
            fontFamily = "Segoe UI"
            textFill = ColorStyles.lightTextColor
        }
        fieldLabel {
            +body
            fontWeight = FontWeight.NORMAL
            fontSize = 17.px
        }
        //region normalText
        root {
            +body
            fontWeight = FontWeight.NORMAL
            fontSize = 14.px
        }
        //endregion normalText
        caption {
            +body
            fontWeight = FontWeight.NORMAL
            fontSize = 12.px
            textFill = ColorStyles.lightCaptionTextColor
        }
        warning {
            textFill = Styles.Orange
            fontWeight = FontWeight.BOLD
        }
        mention {
            textFill = Styles.Blue
            fill = Styles.Blue
            fontWeight = FontWeight.BOLD
        }
    }

}