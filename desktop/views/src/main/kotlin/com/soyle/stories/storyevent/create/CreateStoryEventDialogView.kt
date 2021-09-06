package com.soyle.stories.storyevent.create

import com.soyle.stories.common.components.ComponentsStyles
import com.soyle.stories.common.components.surfaces.Elevation
import com.soyle.stories.common.components.surfaces.Surface.Companion.asSurface
import com.soyle.stories.common.components.text.FieldLabel.Companion.fieldLabel
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.soylestories.Styles
import com.soyle.stories.storyevent.NullableLongSpinnerValueFactory
import com.soyle.stories.usecase.storyevent.create.CreateStoryEvent
import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.application.Platform
import javafx.beans.binding.BooleanExpression
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Spinner
import javafx.scene.control.TextField
import javafx.scene.layout.VBox
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle
import kotlinx.coroutines.*
import org.controlsfx.control.action.ActionMap.action
import tornadofx.*
import tornadofx.Stylesheet.Companion.root
import java.util.*

class CreateStoryEventDialogView(
    private val relativePlacement: CreateStoryEvent.RequestModel.RequestedStoryEventTime.Relative? = null,
    private val createStoryEventController: CreateStoryEventController
) : View() {

    private val stage = Stage(StageStyle.UTILITY)

    private val awaitingSubmission = booleanProperty(false)

    private val nameInput = nameInput()
    private val timeInput: Spinner<Long?>? = if (relativePlacement == null) timeInput() else null
    private val cancelButton: Button = cancelButton()
    private val submitButton: Button = submitButton()

    init {
        submitButton.enableWhen(nameIsValid() and timeIsValid() and awaitingSubmission.not())
        submitButton.action(::submit)
        cancelButton.action(::cancel)
        nameInput.action(::submit)
        timeInput?.editor?.action(::submit)
    }

    private fun submit() {
        val nonBlankName = NonBlankString.create(nameInput.text) ?: return
        awaitSubmission()
        createStoryEventJob(nonBlankName, timeInput?.value).invokeOnCompletion(::completeSubmission)
    }

    private fun cancel() {
        nameInput.text = ""
        timeInput?.valueFactory?.value = null
        stage.hide()
    }

    private fun nameInput() = TextField().apply {
        id = Styles.name.name
    }

    private fun timeInput() = Spinner<Long?>().apply {
        id = Styles.time.name
        isEditable = true
        valueFactory = NullableLongSpinnerValueFactory()
    }

    private fun cancelButton() = Button().apply {
        addClass(Stylesheet.cancel)
        addClass(ComponentsStyles.secondary)
        addClass(ComponentsStyles.outlined)
    }

    private fun submitButton() = Button().apply {
        addClass(Stylesheet.default)
        addClass(ComponentsStyles.primary)
        addClass(ComponentsStyles.filled)
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

    private fun createStoryEventJob(name: NonBlankString, time: Long?): Job {
        return when {
            relativePlacement != null -> createStoryEventController.createStoryEvent(
                name,
                relativePlacement
            )
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
            stage.hide()
        }
    }

    override val root: Parent = VBox().apply {
        isFillWidth = true
        asSurface {
            absoluteElevation = Elevation.get(16)!!
        }
        vbox(spacing = 8) {
            style { padding = box(12.px) }
            isFillWidth = true
            vbox(spacing = 4) {
                isFillWidth = true
                fieldLabel("Name").labelFor = nameInput
                add(nameInput)
            }
            if (timeInput != null) vbox(spacing = 4) {
                isFillWidth = true
                fieldLabel("Time").labelFor = timeInput
                add(timeInput.apply {
                    styleClass.clear()
                    maxWidth = Double.MAX_VALUE
                })
            }
        }
        hbox(spacing = 8, alignment = Pos.BASELINE_RIGHT) {
            style { padding = box(12.px) }
            add(cancelButton.apply {
                text = "cancel".uppercase(Locale.getDefault())
            })
            add(submitButton.apply {
                text = "create".uppercase(Locale.getDefault())
            })
        }
    }

    init {
        root.properties[UI_COMPONENT_PROPERTY] = this // because tornadofx doesn't do it without using its DI framework
    }

    init {
        stage.apply {
            title = "Create New Story Event"
            initModality(Modality.APPLICATION_MODAL)
            scene = Scene(root)
            FX.applyStylesheetsTo(scene)
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