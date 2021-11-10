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
    private val viewModel: CreateStoryEventPrompt
) : Fragment() {

    init {
        title = "Create New Story Event"
    }

    override val root: Parent = vbox {
        asSurface { absoluteElevation = Elevation[16]!! }
        addClass(Styles.createStoryEventPrompt)
        vbox {
            addClass(Stylesheet.form)
            vbox {
                fieldLabel("Name").labelFor = textfield {
                    id = "name"
                    disableWhen(viewModel.submitting())
                    viewModel.name().cleanBind(textProperty())
                    action(viewModel::submit)
                }
            }
            vbox {
                existsWhen(viewModel.timeFieldShown())
                fieldLabel("Time").labelFor = spinner<Long?>(editable = true) {
                    disableWhen(viewModel.submitting())
                    viewModel.timeText().cleanBind(editor.textProperty())
                    valueFactory = NullableLongSpinnerValueFactory()
                    editor.action(viewModel::submit)
                }
            }
        }
        hbox {
            addClass(Stylesheet.buttonBar)
            button("Create") {
                disableWhen(viewModel.submitting())
                addClass(ComponentsStyles.primary, ComponentsStyles.filled)
                isDefaultButton = true
                enableWhen(viewModel.canSubmit())
                action(viewModel::submit)
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