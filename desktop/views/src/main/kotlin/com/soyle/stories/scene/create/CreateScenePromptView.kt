package com.soyle.stories.scene.create

import com.soyle.stories.common.components.ComponentsStyles
import com.soyle.stories.common.components.dataDisplay.decorator.asDecorator
import com.soyle.stories.common.components.dataDisplay.decorator.singleDecorator
import com.soyle.stories.common.components.surfaces.Elevation
import com.soyle.stories.common.components.surfaces.Surface.Companion.asSurface
import com.soyle.stories.common.components.text.FieldLabel.Companion.fieldLabel
import tornadofx.*

class CreateScenePromptView(
    private val viewModel: CreateScenePromptViewModel
) : Fragment() {

    init {
        title = "Create Scene"
    }

    override val root = vbox {
        asSurface { absoluteElevation = Elevation[16]!! }
        addClass(Stylesheet.form)
        vbox {
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



}