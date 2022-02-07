package com.soyle.stories.storyevent.rename

import com.soyle.stories.common.components.ComponentsStyles.Companion.filled
import com.soyle.stories.common.components.ComponentsStyles.Companion.outlined
import com.soyle.stories.common.components.ComponentsStyles.Companion.primary
import com.soyle.stories.common.components.ComponentsStyles.Companion.secondary
import com.soyle.stories.common.components.surfaces.Elevation
import com.soyle.stories.common.components.surfaces.elevation
import com.soyle.stories.common.components.text.FieldLabel.Companion.fieldLabel
import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.layout.VBox
import tornadofx.*
import tornadofx.Stylesheet.Companion.buttonBar
import tornadofx.Stylesheet.Companion.field
import tornadofx.Stylesheet.Companion.form

class RenameStoryEventPromptView(
    private val viewModel: RenameStoryEventPromptViewModel,
    private val actions: RenameStoryEventPromptUserActions
) : Fragment() {

    init {
        title = "Rename Story Event"
    }

    override val root: Parent = vbox {
        addClass(Styles.renameStoryEventPrompt)
        elevation = Elevation.getValue(16)
        vbox {
            addClass(field)
            fieldLabel("New Name").labelFor = textfield {
                text = viewModel.name.value
                viewModel.nameProperty().bind(textProperty())
                disableWhen(viewModel.isDisabled)
                action(actions::rename)
            }
        }
        hbox {
            addClass(buttonBar)
            button("RENAME") {
                addClass(primary, filled)
                isDefaultButton = true
                enableWhen(viewModel.isValid and viewModel.isEnabled)
                action(actions::rename)
            }
            button("CANCEL") {
                addClass(secondary, outlined)
                isCancelButton = true
                action(actions::cancel)
            }
        }
    }

    init {
        root.properties[UI_COMPONENT_PROPERTY] = this
    }

    class Styles : Stylesheet() {

        companion object {

            val renameStoryEventPrompt by cssclass()

            init {
                if (Platform.isFxApplicationThread()) importStylesheet<Styles>()
                else runLater { importStylesheet<Styles>() }
            }

        }

        init {
            renameStoryEventPrompt {
                field {
                    padding = box(12.px)
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