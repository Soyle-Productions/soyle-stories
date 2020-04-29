package com.soyle.stories.project.startProjectDialog

import com.soyle.stories.common.async
import com.soyle.stories.di.resolve
import com.soyle.stories.project.projectList.ProjectListViewListener
import com.soyle.stories.project.startProjectDialog.Styles.Companion.errorState
import com.soyle.stories.soylestories.ApplicationModel
import com.soyle.stories.soylestories.ApplicationScope
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.util.StringConverter
import tornadofx.*
import java.io.File

class StartProjectDialog : View("Start New Project") {

    override val scope: ApplicationScope = super.scope as ApplicationScope

    private val projectListViewListener: ProjectListViewListener = resolve()
    private val model = find<ApplicationModel>().startProjectFailure

    private val selectedDirectoryFile = SimpleObjectProperty<File?>(null)
    private val projectName = SimpleStringProperty("Untitled")
    private val fullFile = SimpleObjectProperty<File?>(null)
    private val isValid = SimpleBooleanProperty()

    init {
        fullFile.bind(selectedDirectoryFile.objectBinding(projectName) {
            if (it == null) return@objectBinding null
            File(it.absolutePath + File.separator + projectName + ".stry")
        })
        isValid.bind(fullFile.booleanBinding {
            val directory = selectedDirectoryFile.value ?: return@booleanBinding false
            it != null && !projectName.value.isNullOrBlank() && directory.isDirectory && ! it.exists()
        })
    }

    override val root: Parent = form {
        fieldset {
            field("Directory") {
                labelContainer.alignment = Pos.CENTER_RIGHT
                hbox(5) {
                    textfield {
                        toggleClass(errorState, model.select { (it.failingField == "directory").toProperty() })
                        hgrow = Priority.ALWAYS
                        textProperty().bindBidirectional(selectedDirectoryFile, object : StringConverter<File?>() {
                            override fun fromString(p0: String?): File = File(p0 ?: "")
                            override fun toString(p0: File?): String = p0?.absolutePath ?: ""
                        })
                    }
                    button("Choose Directory") {
                        runLater { requestFocus() }
                        action {
                            selectedDirectoryFile.set(chooseDirectory("Choose Directory", owner = currentStage))
                        }
                    }
                }
            }
            field("Project Name") {
                labelContainer.alignment = Pos.CENTER_RIGHT
                textfield {
                    toggleClass(errorState, model.select { (it.failingField == "name").toProperty() })
                    textProperty().bindBidirectional(projectName)
                    focusedProperty().onChangeOnce {
                        if (it == true) selectAll()
                    }
                }
            }
        }
        label {
            val messageProperty = model.select { it.message.toProperty() }
            textProperty().bind(messageProperty)
            visibleWhen { messageProperty.isNotBlank() }
            style {
                textFill = Color.RED
            }
        }
        hbox(5) {
            alignment = Pos.BOTTOM_RIGHT
            button("Cancel") {
                isCancelButton = true
                action {
                    close()
                }
            }
            button("Create Project") {
                enableWhen { isValid }
                isDefaultButton = true
                action {
                    async(scope) {
                        projectListViewListener.startNewProject(selectedDirectoryFile.value!!.absolutePath, projectName.value)
                        runLater {
                            if (model.value == null) {
                                close()
                            }
                        }
                    }
                }
            }
        }
    }

}

fun Component.startProjectDialog(scope: ApplicationScope, owner: Stage?): StartProjectDialog = find(StartProjectDialog::class, scope = scope).apply {
    openModal(
        stageStyle = StageStyle.UTILITY,
        modality = Modality.APPLICATION_MODAL,
        escapeClosesWindow = true,
        owner = owner,
        block = false,
        resizable = true
    )?.apply {
        centerOnScreen()
    }
}

class Styles : Stylesheet() {
    companion object {
        val errorState by cssclass()

        init {
            importStylesheet<Styles>()
        }
    }

    init {
        errorState {
            borderColor += box(Color.RED)
            borderWidth += box(1.px)
        }
    }

}