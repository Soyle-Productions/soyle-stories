package com.soyle.stories.storyevent.rename

import com.soyle.stories.common.onChangeWithCurrent
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.validation.NonBlankString
import javafx.beans.property.ReadOnlyBooleanWrapper
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.TextField
import javafx.scene.layout.Pane
import javafx.stage.Stage
import tornadofx.*

class RenameStoryEventDialogView(
    private val storyEventId: StoryEvent.Id,
    currentName: String,
    private val renameStoryEventController: RenameStoryEventController
) : View() {

    private val submitting = booleanProperty(false)

    private val cancelButton = Button().apply {
        addClass(Stylesheet.cancel)
        action(::cancel)
        disableWhen(submitting)
    }
    private val nameInput = TextField().apply {
        text = currentName
        disableWhen(submitting)
    }
    private val submitButton = Button().apply {
        addClass(Stylesheet.default)
        enableWhen(nameIsValid(currentName).and(submitting.not()))
        action(::submit)
    }

    private fun nameIsValid(currentName: String) = with(nameInput.textProperty()) {
        isNotEmpty.and(isNotEqualTo(currentName))
    }

    override val root: Parent = Pane().apply {
        add(nameInput)
        add(submitButton)
        add(cancelButton)
    }

    private val cancelledProperty = lazy { ReadOnlyBooleanWrapper(false) }
    fun cancelledProperty() = cancelledProperty.value.readOnlyProperty
    var isCancelled: Boolean
        get() = cancelledProperty.value.get()
        private set(value) { cancelledProperty.value.set(value) }
    fun onCancelled(listener: () -> Unit) {
        cancelledProperty.value.onChangeWithCurrent { if (it == true) listener() }
    }

    private fun cancel() { isCancelled = true }

    private val completedProperty = lazy { ReadOnlyBooleanWrapper(false) }
    fun completedProperty() = completedProperty.value.readOnlyProperty
    var isCompleted: Boolean
        get() = completedProperty.value.get()
        private set(value) { completedProperty.value.set(value) }
    fun onCompleted(listener: () -> Unit) {
        completedProperty.value.onChangeWithCurrent { if (it == true) listener() }
    }

    private fun submit() {
        val name = NonBlankString.create(nameInput.text) ?: return

        submitting.set(true)
        renameStoryEventController.renameStoryEvent(storyEventId, name)
            .invokeOnCompletion(::complete)
    }

    private fun complete(failure: Throwable?) {
        submitting.set(false)
        if (failure == null) {
            isCompleted = true
        }
    }

    init {
        root.properties[UI_COMPONENT_PROPERTY] = this // because tornadofx doesn't do it without using its DI framework
    }

    init {
        Stage().apply {
            scene = Scene(root)
            show()
        }
    }

}