package com.soyle.stories.storyevent.remove

import com.soyle.stories.common.components.ComponentsStyles.Companion.filled
import com.soyle.stories.common.components.ComponentsStyles.Companion.outlined
import com.soyle.stories.common.components.ComponentsStyles.Companion.primary
import com.soyle.stories.common.components.ComponentsStyles.Companion.secondary
import javafx.scene.Parent
import javafx.scene.control.*
import tornadofx.*
import javax.swing.text.Style

class RemoveStoryEventConfirmationPromptView(
    private val actions: RemoveStoryEventConfirmationPromptViewActions,
    private val viewModel: RemoveStoryEventConfirmationPromptViewModel
) : Fragment() {

    init {
        title = "Confirm Removal"
    }

    override val root: Parent = DialogPane().apply {
        addClass(Stylesheet.alert, Stylesheet.confirmation, Stylesheet.header)
        headerText = "Are you sure you want to delete this/these story event(s)?"
        content = checkbox {
            id = "show-again"
            text = "Don't show this dialog again"
            viewModel.shouldNotShowAgain().bind(selectedProperty())
            enableWhen(viewModel.canConfirm())
        }

        buttonTypes.setAll(ButtonType.OK, ButtonType.CANCEL)
        lookupButton(ButtonType.OK).apply {
            id = "confirm"
            addClass(primary, filled)
            enableWhen(viewModel.canConfirm())
            if (this is ButtonBase) {
                action(actions::confirm)
            }
        }
        lookupButton(ButtonType.CANCEL).apply {
            id = "cancel"
            addClass(secondary, outlined)
            if (this is ButtonBase) {
                action(actions::cancel)
            }
        }
    }

    init {
        root.properties[UI_COMPONENT_PROPERTY] = this
    }
}