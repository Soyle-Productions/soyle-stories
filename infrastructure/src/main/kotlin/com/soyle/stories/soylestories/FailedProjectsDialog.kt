package com.soyle.stories.soylestories

import com.soyle.stories.common.NoSelectionModel
import com.soyle.stories.project.FailedProject
import com.soyle.stories.project.projectList.ProjectIssueViewModel
import javafx.geometry.Pos
import javafx.stage.Modality
import javafx.stage.StageStyle
import tornadofx.*

/**
 * Created by Brendan
 * Date: 2/16/2020
 * Time: 12:02 PM
 */
class FailedProjectsDialog : View("Failed Projects") {

    private val model = find<ApplicationModel>()

    override val root = vbox {
        prefHeight = 400.0
        prefWidth = 400.0
        spacing = 10.0
        paddingAll = 10.0
        label("The following projects failed to load:")
        listview<ProjectIssueViewModel> {
            selectionModel = NoSelectionModel()
            isFocusTraversable = false
            cellFragment(fragment = FailedProject::class)
            itemsProperty().bind(model.failedProjects)
        }
        hbox(alignment = Pos.CENTER_RIGHT) {
            button("Ignore All") {
                action {

                }
            }
        }
    }

    init {
        model.isFailedProjectDialogVisible.onChange {
            if (it == true) openModal(
                StageStyle.UTILITY,
                Modality.APPLICATION_MODAL,
                escapeClosesWindow = false,
                owner = null,
                block = true,
                resizable = true
            ) else close()
        }
    }
}
