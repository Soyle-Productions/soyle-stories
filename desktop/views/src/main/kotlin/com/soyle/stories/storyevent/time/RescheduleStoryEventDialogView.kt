package com.soyle.stories.storyevent.time

import com.soyle.stories.storyevent.NullableLongSpinnerValueFactory
import com.soyle.stories.storyevent.time.reschedule.RescheduleStoryEventController
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.control.Spinner
import tornadofx.*

class RescheduleStoryEventDialogView(
    private val props: RescheduleStoryEventDialog.Props,
    private val rescheduleStoryEventController: RescheduleStoryEventController
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
        valueFactory.value = props.currentTime
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
                timeInput.valueProperty().isEqualTo(props.currentTime)

    private fun submit() {
        submitting.set(true)
        rescheduleStoryEventController.rescheduleStoryEvent(props.storyEventId, timeInput.value!!)
            .invokeOnCompletion {
                runLater {
                    submitting.set(false)
                    if (it == null) stage.hide()
                }
            }
    }

    private val stage = openModal()!!

}