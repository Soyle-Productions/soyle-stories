package com.soyle.stories.storyevent.create

import com.soyle.stories.characterarc.components.characterIcon
import com.soyle.stories.common.components.ComponentsStyles
import com.soyle.stories.common.components.surfaces.Elevation
import com.soyle.stories.common.components.surfaces.Surface
import com.soyle.stories.common.components.surfaces.Surface.Companion.asSurface
import com.soyle.stories.common.components.text.FieldLabel.Companion.fieldLabel
import com.soyle.stories.common.existsWhen
import com.soyle.stories.storyevent.NullableLongSpinnerValueFactory
import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.layout.VBox
import org.controlsfx.control.action.ActionMap.action
import tornadofx.*

class CreateStoryEventPromptView(
    private val actions: CreateStoryEventPromptUserActions,
    private val viewModel: CreateStoryEventPromptViewModel
) : Fragment() {

    init {
        title = "Create New Story Event"
    }

    override val root: Parent = vbox {
        asSurface { absoluteElevation = Elevation.get(16)!! }
        addClass(Styles.createStoryEventPrompt)
        vbox {
            addClass(Stylesheet.form)
            vbox {
                fieldLabel("Name").labelFor = textfield {
                    id = "name"
                    disableWhen(viewModel.isCreating)
                    viewModel.name.bind(textProperty())
                    action(actions::createStoryEvent)
                }
            }
            vbox {
                existsWhen(viewModel.timeNotAlreadySpecified)
                fieldLabel("Time").labelFor = spinner<Long?>(editable = true) {
                    disableWhen(viewModel.isCreating)
                    viewModel.timeText.bind(editor.textProperty())
                    valueFactory = NullableLongSpinnerValueFactory()
                    editor.action(actions::createStoryEvent)
                }
            }
        }
        hbox {
            addClass(Stylesheet.buttonBar)
            button("Create") {
                disableWhen(viewModel.isCreating)
                addClass(ComponentsStyles.primary, ComponentsStyles.filled)
                isDefaultButton = true
                enableWhen(viewModel.isValid)
                action(actions::createStoryEvent)
            }
            button("Cancel") {
                addClass(ComponentsStyles.secondary, ComponentsStyles.outlined)
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

            val createStoryEventPrompt by cssclass()

            init {
                if (Platform.isFxApplicationThread()) importStylesheet<Styles>()
                else runLater { importStylesheet<Styles>() }
            }
        }

        init {
            createStoryEventPrompt {
                fillWidth = true
                form {
                    padding = box(12.px)
                    spacing = 8.px
                    fillWidth = true
                    spinner {
                        maxWidth = Double.MAX_VALUE.px
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