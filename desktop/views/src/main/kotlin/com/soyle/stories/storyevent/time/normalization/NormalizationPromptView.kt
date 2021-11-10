package com.soyle.stories.storyevent.time.normalization

import com.soyle.stories.common.components.ComponentsStyles
import com.soyle.stories.common.components.surfaces.Elevation
import com.soyle.stories.common.components.surfaces.Surface.Companion.asSurface
import com.soyle.stories.common.components.text.SectionTitle.Companion.sectionTitle
import com.soyle.stories.common.components.text.TextStyles.Companion.section
import com.soyle.stories.common.components.text.TextStyles.Companion.sectionTitle
import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.layout.Priority
import tornadofx.*

class NormalizationPromptView(
    submit: () -> Unit
) : Fragment() {

    override val root: Parent = vbox {
        asSurface { absoluteElevation = Elevation[16]!! }
        addClass(Styles.normalizationPrompt)
        vbox {
            addClass(Stylesheet.form)
            sectionTitle(
                "Some story events may end up in the negatives.  This is not currently allowed, "+
                        "so the application will normalize the entire story timeline so that they are all above zero."
            ) {
                vgrow = Priority.SOMETIMES
            }
        }
        hbox {
            addClass(Stylesheet.buttonBar)
            button("Ok") {
                addClass(ComponentsStyles.primary, ComponentsStyles.filled)
                isDefaultButton = true
                action(submit)
            }
            button("Cancel") {
                addClass(ComponentsStyles.secondary, ComponentsStyles.outlined)
                isCancelButton = true
                action { close() }
            }
        }
    }

    class Styles : Stylesheet() {

        companion object {

            val normalizationPrompt by cssclass()

            init {
                if (Platform.isFxApplicationThread()) importStylesheet<Styles>()
                else runLater { importStylesheet<Styles>() }
            }
        }

        init {
            normalizationPrompt {
                maxWidth = 30.em
                fillWidth = true
                form {
                    padding = box(12.px)
                    spacing = 8.px
                    fillWidth = true
                    sectionTitle {
                        wrapText = true
                        minWidth = 0.px
                    }
                }
                buttonBar {
                    padding = box(12.px)
                    spacing = 8.px
                    alignment = Pos.CENTER_RIGHT
                }
            }
        }

    }

}