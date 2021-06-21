package com.soyle.stories.location.createLocationDialog

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.common.ViewBuilder
import com.soyle.stories.common.components.text.WindowTitle.Companion.windowTitle
import com.soyle.stories.common.onChangeUntil
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
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.stage.Window
import tornadofx.*

class CreateLocationDialog(
    private val onCreateLocation: (CreateNewLocation.ResponseModel) -> Unit,
    private val createLocationController: CreateNewLocationController
) {

    interface Factory {
        operator fun invoke(onCreateLocation: (CreateNewLocation.ResponseModel) -> Unit = {}): CreateLocationDialog
    }

    val name = SimpleStringProperty("")
    val description = SimpleStringProperty("")
    val errorMessage = stringProperty(null)

    inner class View : Form() {

        init {
            fieldset {
                field("Name") {
                    textfield {
                        id = "name"
                        name.bind(textProperty())
                        requestFocus()
                        onAction = EventHandler {
                            it.consume()
                            ifNameIsValid {
                                awaitCreateLocation(it, description.value)
                            }
                        }
                    }
                }
                field("Description (Optional)") {
                    textfield {
                        description.bind(textProperty())
                    }
                }
                field {
                    text(errorMessage) {
                        id = "errorMessage"
                    }
                }
            }
            hbox {
                button("Create") {
                    id = "createLocation"
                    action {
                        ifNameIsValid {
                            awaitCreateLocation(it, description.value)
                        }
                    }
                }
                button("Cancel") {
                    id = "cancel"
                    action {
                        scene?.window?.hide()
                    }
                }
            }
        }

    }

    private val root: Parent = View()

    private fun ifNameIsValid(block: (SingleNonBlankLine) -> Unit) {
        val nameLineCount = countLines(name.value)
        if (nameLineCount is SingleLine) {
            val nonBlankName = SingleNonBlankLine.create(nameLineCount)
            if (nonBlankName != null) block(nonBlankName)
        }
    }

    private fun awaitCreateLocation(name: SingleNonBlankLine, description: String) {
        root.isDisable = true
        val deferred = createLocationController.createNewLocation(name, description)
        deferred
            .invokeOnCompletion {
                root.isDisable = false

                if (it != null) {
                    errorMessage.set(it.localizedMessage)
                } else {
                    root.scene?.window?.hide()
                    onCreateLocation(deferred.getCompleted())
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
            show()
            centerOnScreen()
        }
    }

}