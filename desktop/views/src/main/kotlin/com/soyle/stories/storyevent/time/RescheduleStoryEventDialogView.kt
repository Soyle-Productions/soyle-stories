package com.soyle.stories.storyevent.time

import com.soyle.stories.storyevent.NullableLongSpinnerValueFactory
import com.soyle.stories.storyevent.time.adjust.AdjustStoryEventsTimeController
import com.soyle.stories.storyevent.time.reschedule.RescheduleStoryEventController
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.control.Spinner
import tornadofx.*

class RescheduleStoryEventDialogView(
    private val props: RescheduleStoryEventDialog.Props,
    private val rescheduleStoryEventController: RescheduleStoryEventController,
    private val adjustStoryEventsTimeController: AdjustStoryEventsTimeController
) : Fragment() {

    private val submitting = booleanProperty(false)

    private val timeInput = timeInput()
    private val saveButton = saveButton()
    private val cancelButton = cancelButton()

    override val root: Parent = pane {
        add(timeInput)
        add(saveButton)
        add(cancelButton)
    }

    private fun timeInput() = Spinner<Long?>().apply {
        isEditable = true
        valueFactory = NullableLongSpinnerValueFactory()
        id = "time"
        valueFactory.value = (props as? RescheduleStoryEventDialog.Reschedule)?.currentTime
        disableWhen(submitting)
    }

    private fun saveButton() = Button().apply {
        id = "save"
        disableWhen(timeValueIsInvalid() or submitting)
        action(::submit)
    }

    private fun cancelButton() = Button().apply {
        id = "cancel"
        disableWhen(submitting)
        action {
            stage.hide()
        }
    }

    private fun timeValueIsInvalid() =
        timeInput.editor.textProperty().isBlank() or
                timeInput.valueProperty().isEqualTo((props as? RescheduleStoryEventDialog.Reschedule)?.currentTime ?: 0)

    private fun submit() {
        submitting.set(true)
       val submission = when (props) {
            is RescheduleStoryEventDialog.Reschedule -> rescheduleStoryEventController.rescheduleStoryEvent(props.storyEventId, timeInput.value!!)
            is RescheduleStoryEventDialog.AdjustTimes -> adjustStoryEventsTimeController.adjustStoryEventsTime(props.storyEventIds, timeInput.value!!)
        }
        submission
            .invokeOnCompletion {
                runLater {
                    submitting.set(false)
                    if (it == null) stage.hide()
                }
            }
    }

    private val stage = openModal()!!

}