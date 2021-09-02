package com.soyle.stories.storyevent.remove

import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.writer.setDialogPreferences.SetDialogPreferencesController
import javafx.scene.Parent
import javafx.scene.control.Alert
import javafx.scene.control.ButtonBase
import javafx.scene.control.ButtonType
import javafx.scene.control.DialogPane
import tornadofx.*

class RemoveStoryEventConfirmationDialogView(
    private val storyEventIds: Set<StoryEvent.Id>,
    private val removeStoryEventController: RemoveStoryEventController,
    private val setDialogPreferencesController: SetDialogPreferencesController
) : Fragment() {

    private val showingProperty = booleanProperty(true)
    private val showAgainProperty = booleanProperty(true)

    override val root: Parent = DialogPane().apply {
        checkbox {
            id = "show-again"
            isSelected = true
            showAgainProperty.bind(selectedProperty())
        }

        buttonTypes.setAll(ButtonType.OK, ButtonType.CANCEL)
        lookupButton(ButtonType.OK).apply {
            id = "confirm"
            if (this is ButtonBase) {
                action(::confirm)
            }
        }
        lookupButton(ButtonType.CANCEL).apply {
            id = "cancel"
            if (this is ButtonBase) {
                action(::cancel)
            }
        }
    }

    private fun confirm() {
        removeStoryEventController.confirmRemoveStoryEvent(storyEventIds).invokeOnCompletion {
            if (it == null) runLater { showingProperty.set(false) }
        }
        setDialogPreferencesController.setDialogPreferences("DeleteStoryEvent", showAgainProperty.value)
    }

    private fun cancel() {
        showingProperty.set(false)
    }

    init {
        val stage = openModal()
        showingProperty.onChangeOnce { stage?.hide() }
    }

}