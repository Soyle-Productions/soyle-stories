package com.soyle.stories.location.createLocationDialog

import com.soyle.stories.common.*
import com.soyle.stories.common.components.ComponentsStyles
import com.soyle.stories.common.components.buttons.primaryButton
import com.soyle.stories.common.components.buttons.secondaryButton
import com.soyle.stories.common.components.surfaces.Elevation
import com.soyle.stories.common.components.surfaces.Surface.Companion.asSurface
import com.soyle.stories.common.components.text.FieldLabel.Companion.fieldLabel
import com.soyle.stories.common.components.text.WindowTitle.Companion.windowTitle
import com.soyle.stories.di.get
import com.soyle.stories.di.resolve
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.validation.SingleLine
import com.soyle.stories.domain.validation.SingleNonBlankLine
import com.soyle.stories.domain.validation.countLines
import com.soyle.stories.location.controllers.CreateNewLocationController
import com.soyle.stories.location.events.CreateNewLocationNotifier
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.WorkBench
import com.soyle.stories.usecase.location.createNewLocation.CreateNewLocation
import javafx.beans.property.SimpleStringProperty
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.stage.Window
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.withContext
import tornadofx.*

class CreateLocationDialog(
    private val threadTransformer: ThreadTransformer,
    private val onCreateLocation: suspend (CreateNewLocation.ResponseModel) -> Unit,
    private val createLocationController: CreateNewLocationController,
    private val locale: CreateLocationDialogLocale
) {

    interface Factory {
        operator fun invoke(onCreateLocation: suspend (CreateNewLocation.ResponseModel) -> Unit = {}): CreateLocationDialog
    }

    private val name = SimpleStringProperty("")
    private val description = SimpleStringProperty("")
    private val errorMessage = stringProperty(null)

    inner class View : VBox() {

        init {
            asSurface {
                absoluteElevation = Elevation.getValue(24)
            }
            padding = Insets(16.0)
            spacing = 16.0
            isFillWidth = true
        }

        init {
            vbox {
                spacing = 4.0
                val label = fieldLabel(locale.name)
                val input = textfield {
                    id = Styles.name.name
                    name.bind(textProperty())
                    scopedListener(errorMessage) {
                        decorators.clear()
                        if (it != null) addDecorator(SimpleMessageDecorator(it, ValidationSeverity.Error))
                    }
                    action(::attemptCreateLocation)
                }
                label.labelFor = input
            }
            vbox {
                spacing = 4.0
                val label = fieldLabel(locale.description)
                val input = textfield {
                    id = Styles.description.name
                    description.bind(textProperty())
                }
                label.labelFor = input
            }
            spacer()
            hbox {
                alignment = Pos.BOTTOM_RIGHT
                spacing = 8.0
                primaryButton(locale.create) {
                    isDefaultButton = true
                    disableWhen(name.isEmpty)
                    action(::attemptCreateLocation)
                }
                secondaryButton(locale.cancel) {
                    isCancelButton = true
                    action {
                        scene?.window?.hide()
                    }
                }
            }
        }

    }

    private val root: Region by lazy { View() }

    private fun attemptCreateLocation() {
        errorMessage.unbind()
        errorMessage.set(null)
        val nameLineCount = (countLines(name.value) as? SingleLine)?.let(SingleNonBlankLine::create)
        if (nameLineCount != null) {
            awaitCreateLocation(nameLineCount, description.value)
        } else {
            errorMessage.bind(locale.pleaseProvideALocationName)
        }
    }

    private fun awaitCreateLocation(name: SingleNonBlankLine, description: String) {
        root.isDisable = true
        threadTransformer.async {
            val deferred = createLocationController.createNewLocation(name, description)
            val creationResponse = try {
                deferred.await()
            } finally {
                guiUpdate {
                    root.isDisable = false
                }
            }
            onCreateLocation(creationResponse)
            guiUpdate {
                root.scene.window.hide()
            }
        }
    }

    fun show(owner: Window? = null)
    {
        Stage(StageStyle.UTILITY).apply {
            if (owner != null) initOwner(owner)
            initModality(Modality.APPLICATION_MODAL)
            addEventFilter(KeyEvent.KEY_PRESSED) {
                if (it.code == KeyCode.ESCAPE)
                    close()
            }
            scene = Scene(root)
            root.fitToParentSize()
            titleProperty().bind(locale.newLocation)
            show()
            centerOnScreen()
        }
    }

    class Styles : Stylesheet()
    {
        companion object {
            val name by cssid()
            val description by cssid()
        }



    }

}