package com.soyle.stories.storyevent.create

import com.soyle.stories.domain.validation.NonBlankString
import javafx.application.Platform
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Spinner
import javafx.scene.control.SpinnerValueFactory
import javafx.scene.control.TextField
import javafx.scene.layout.Pane
import javafx.util.StringConverter
import kotlinx.coroutines.runBlocking
import tornadofx.*

class CreateStoryEventForm(
    private val createStoryEventController: CreateStoryEventController
) {

    private val nameInput = TextField().apply {
        id = Styles.name.name
    }
    private val timeInput = Spinner<Long?>().apply {
        id = Styles.time.name
        isEditable = true
        valueFactory = object : SpinnerValueFactory<Long?>() {
            override fun increment(steps: Int) {
                value = value?.plus(steps)
            }

            override fun decrement(steps: Int) = increment(-steps)

            init {
                value = null
                converter = object : StringConverter<Long?>() {
                    override fun toString(`object`: Long?): String = `object`?.toString() ?: ""
                    override fun fromString(string: String?): Long? = string?.toLongOrNull()
                }
            }
        }
    }

    private val submitButton = Button().apply {
        addClass(Stylesheet.default)
        enableWhen(nameInput.textProperty().isNotBlank().and(timeInput.valueProperty().isNotNull))
        action(::submit)
    }
    private val cancelButton = Button().apply {
        addClass(Stylesheet.cancel)
        action(::cancel)
    }

    private fun cancel() {
        nameInput.text = ""
        timeInput.valueFactory.value = null
        if (onCancelProperty.isInitialized()) onCancel?.invoke()
    }

    private val onCancelProperty = lazy { objectProperty<(() -> Unit)?>(null) }
    fun onCancelProperty() = onCancelProperty.value
    var onCancel by onCancelProperty()
    fun onCancel(listener: () -> Unit) {
        onCancel = listener
    }

    private val onCreateProperty = lazy { objectProperty<(() -> Unit)?>(null) }
    fun onCreateProperty() = onCreateProperty.value
    var onCreate by onCreateProperty()
    fun onCreate(listener: () -> Unit) {
        onCreate = listener
    }

    private fun submit() {
        nameInput.isDisable = true
        timeInput.isDisable = true

        val nonBlankName = NonBlankString.create(nameInput.text) ?: return

        createStoryEventController.createStoryEvent(nonBlankName)
            .invokeOnCompletion(::completeSubmission)
    }

    private fun completeSubmission(potentialFailure: Throwable?) {
        runLater {
            nameInput.isDisable = false
            timeInput.isDisable = false
            if (potentialFailure == null) {
                nameInput.text = ""
                timeInput.valueFactory.value = null
                if (onCancelProperty.isInitialized()) onCreate?.invoke()
            }
        }
    }

    val root: Node = Pane().apply {
        add(nameInput)
        add(timeInput)
        add(submitButton)
        add(cancelButton)
    }

    class Styles : Stylesheet() {
        companion object {

            val name by cssid()
            val time by cssid()

            init {
                if (Platform.isFxApplicationThread()) importStylesheet<Styles>()
                else runLater { importStylesheet<Styles>() }
            }
        }
    }

}