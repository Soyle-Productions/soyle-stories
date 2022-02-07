package com.soyle.stories.scene.create

import com.soyle.stories.common.components.ComponentsStyles
import com.soyle.stories.common.components.dataDisplay.decorator.asDecorator
import com.soyle.stories.common.components.dataDisplay.decorator.singleDecorator
import com.soyle.stories.common.components.surfaces.Elevation
import com.soyle.stories.common.components.surfaces.elevation
import com.soyle.stories.common.components.text.FieldLabel.Companion.fieldLabel
import com.soyle.stories.common.styleImporter
import com.soyle.stories.scene.create.CreateScenePromptView.Styles.Companion.createScenePrompt
import javafx.geometry.Pos
import tornadofx.*

class CreateScenePromptView(
    private val viewModel: CreateScenePromptViewModel
) : Fragment() {

    init {
        title = "Create Scene"
    }

    override val root = vbox {
        elevation = Elevation.getValue(16)
        addClass(createScenePrompt)
        vbox {
            addClass(Stylesheet.form)
            fieldLabel("Name").labelFor = textfield {
                disableWhen(viewModel.submitting())
                requestFocus()
                viewModel.name().bindBidirectional(textProperty())
                singleDecorator().bind(viewModel.errorMessage().asDecorator(ValidationSeverity.Error))
                action(viewModel::submit)
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
            val createScenePrompt by cssclass()

            init {
                styleImporter<Styles>()
            }
        }

        init {
            createScenePrompt {

                form {

                    padding = box(24.px)

                }

                buttonBar {
                    alignment = Pos.CENTER_RIGHT
                    spacing = 8.px
                    padding = box(8.px)
                }

            }
        }

    }

}