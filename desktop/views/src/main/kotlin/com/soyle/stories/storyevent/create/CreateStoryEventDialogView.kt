package com.soyle.stories.storyevent.create

import com.soyle.stories.domain.validation.NonBlankString
import javafx.application.Platform
import javafx.beans.binding.BooleanBinding
import javafx.beans.binding.BooleanExpression
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Spinner
import javafx.scene.control.SpinnerValueFactory
import javafx.scene.control.TextField
import javafx.scene.layout.VBox
import javafx.stage.Stage
import javafx.util.StringConverter
import kotlinx.coroutines.*
import kotlinx.coroutines.javafx.JavaFx
import tornadofx.*

class CreateStoryEventDialogView(
    private val props: CreateStoryEventDialog.Props,
    private val createStoryEventController: CreateStoryEventController
) : View() {

    private val stage = Stage()

    private val awaitingSubmission = booleanProperty(false)

    private val nameInput = nameInput()
    private val timeInput: Spinner<Long?>? = if (props.relativePlacement == null) timeInput() else null
    private val cancelButton: Button = cancelButton()
    private val submitButton: Button = submitButton()

    init {
        submitButton.enableWhen(nameIsValid() and timeIsValid() and awaitingSubmission.not())
        submitButton.action(::submit)
        cancelButton.action(::cancel)
    }

    private fun submit() {
        val nonBlankName = NonBlankString.create(nameInput.text) ?: return
        awaitSubmission()
        createStoryEventJob(nonBlankName, timeInput?.value).invokeOnCompletion(::completeSubmission)
    }

    private fun cancel() {
        nameInput.text = ""
        timeInput?.valueFactory?.value = null
        props.onCancelled?.invoke()
        stage.hide()
    }

    private fun nameInput() = TextField().apply {
        id = Styles.name.name
    }

    private fun timeInput() = Spinner<Long?>().apply {
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

    private fun cancelButton() = Button().apply {
        addClass(Stylesheet.cancel)
    }

    private fun submitButton() = Button().apply {
        addClass(Stylesheet.default)
    }

    private fun nameIsValid() = nameInput.textProperty().isNotBlank()
    private fun timeIsValid(): BooleanExpression {
        return if (timeInput != null) {
            with(timeInput) {
                editor.textProperty().isBlank().or(valueProperty().isNotNull)
            }
        } else {
            booleanProperty(true)
        }
    }

    private fun awaitSubmission() {
        awaitingSubmission.set(true)
        disableInput()
    }

    private fun disableInput() {
        nameInput.isDisable = true
        timeInput?.isDisable = true
    }

    private fun enableInput() {
        nameInput.isDisable = false
        timeInput?.isDisable = false
    }

    private fun createStoryEventJob(name: NonBlankString, time: Long?): Job
    {
        return when {
            props.relativePlacement != null -> createStoryEventController.createStoryEvent(name, props.relativePlacement)
            time == null -> createStoryEventController.createStoryEvent(name)
            else -> createStoryEventController.createStoryEvent(name, time)
        }
    }

    private fun completeSubmission(potentialFailure: Throwable?) {
        runLater {
            completeSubmissionOnFxThread(potentialFailure)
        }
    }

    private fun completeSubmissionOnFxThread(potentialFailure: Throwable?) {
        awaitingSubmission.set(false)
        enableInput()
        if (potentialFailure == null) {
            nameInput.text = ""
            timeInput?.valueFactory?.value = null
            props.onCreated?.invoke()
            stage.hide()
        }
    }

    override val root: Parent = VBox().apply {
        add(nameInput)
        if (timeInput != null) add(timeInput)
        add(cancelButton)
        add(submitButton)
    }

    init {
        root.properties[UI_COMPONENT_PROPERTY] = this // because tornadofx doesn't do it without using its DI framework
    }

    init {
        stage.apply {
            scene = Scene(root)
            show()
        }
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