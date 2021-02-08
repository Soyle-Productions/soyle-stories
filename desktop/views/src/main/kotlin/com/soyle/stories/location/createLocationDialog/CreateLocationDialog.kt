package com.soyle.stories.location.createLocationDialog

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.common.onChangeUntil
import com.soyle.stories.di.get
import com.soyle.stories.di.resolve
import com.soyle.stories.domain.validation.SingleLine
import com.soyle.stories.domain.validation.SingleNonBlankLine
import com.soyle.stories.domain.validation.countLines
import com.soyle.stories.location.events.CreateNewLocationNotifier
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.WorkBench
import com.soyle.stories.usecase.location.createNewLocation.CreateNewLocation
import javafx.beans.property.SimpleStringProperty
import javafx.event.EventHandler
import javafx.stage.Modality
import javafx.stage.StageStyle
import tornadofx.*

class CreateLocationDialog : Fragment("New Location") {

    override val scope: ProjectScope = super.scope as ProjectScope
    private val model = find<CreateLocationDialogModel>()
    private val createLocationDialogViewListener: CreateLocationDialogViewListener = resolve()

    internal var onCreateLocation: (CreateNewLocation.ResponseModel) -> Unit by singleAssign()

    private val createdLocationNotifier = resolve<CreateNewLocationNotifier>()

    private val createdLocationReceiver = object : CreateNewLocation.OutputPort {
        override fun receiveCreateNewLocationResponse(response: CreateNewLocation.ResponseModel) {
            scope.applicationScope.get<ThreadTransformer>().gui {
                onCreateLocation.invoke(response)
            }
            createdLocationNotifier.removeListener(this)
        }

        override fun receiveCreateNewLocationFailure(failure: Exception) {
            createdLocationNotifier.removeListener(this)
        }
    }

    val name = SimpleStringProperty("")
    val description = SimpleStringProperty("")

    override val root = form {
        fieldset {
            field("Name") {
                textfield {
                    id = "name"
                    name.bind(textProperty())
                    requestFocus()
                    onAction = EventHandler {
                        it.consume()
                        ifNameIsValid {
                            createdLocationNotifier.addListener(createdLocationReceiver)
                            createLocationDialogViewListener.createLocation(it, description.value)
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
                text(model.errorMessage) {
                    id = "errorMessage"
                }
            }
        }
        hbox {
            button("Create") {
                id = "createLocation"
                action {
                    ifNameIsValid {
                        createLocationDialogViewListener.createLocation(it, description.value)
                    }
                }
            }
            button("Cancel") {
                id = "cancel"
                action {
                    close()
                }
            }
        }
    }

    init {
        model.isOpen.onChangeUntil({ it != true }) {
            if (it != true) close()
        }
        createLocationDialogViewListener.getValidState()
    }

    private fun ifNameIsValid(block: (SingleNonBlankLine) -> Unit) {
        val nameLineCount = countLines(name.value)
        if (nameLineCount is SingleLine) {
            val nonBlankName = SingleNonBlankLine.create(nameLineCount)
            if (nonBlankName != null) block(nonBlankName)
        }
    }

}

fun createLocationDialog(
    scope: ProjectScope,
    onCreateLocation: (CreateNewLocation.ResponseModel) -> Unit = {}
): CreateLocationDialog = scope.get<CreateLocationDialog>().apply {
    this.onCreateLocation = onCreateLocation
    openModal(
        StageStyle.UTILITY,
        Modality.APPLICATION_MODAL,
        escapeClosesWindow = true,
        owner = scope.get<WorkBench>().currentWindow
    )?.apply {
        centerOnScreen()
    }
}