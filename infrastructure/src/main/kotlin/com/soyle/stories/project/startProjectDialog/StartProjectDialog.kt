package com.soyle.stories.project.startProjectDialog

import com.soyle.stories.common.launchTask
import com.soyle.stories.di.modules.ApplicationComponent
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.layout.Priority
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.util.StringConverter
import tornadofx.*
import java.io.File

class StartProjectDialog : Fragment("Start New Project") {

    private val projectListViewListener = find<ApplicationComponent>().projectListViewListener

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
                    textProperty().bindBidirectional(projectName)
                    focusedProperty().onChangeOnce {
                        if (it == true) selectAll()
                    }
                }
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
                    launchTask {
                        projectListViewListener.startNewProject(selectedDirectoryFile.value!!.absolutePath, projectName.value)
                    }
                }
            }
        }
    }

}

fun Component.startProjectDialog(owner: Stage?): StartProjectDialog = find(StartProjectDialog::class, scope = FX.defaultScope).apply {
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