package com.soyle.stories.project.projectList.presenters

import com.soyle.stories.project.openProject.ProjectOpenedReceiver
import com.soyle.stories.project.projectList.ProjectFileViewModel
import com.soyle.stories.project.projectList.ProjectIssueViewModel
import com.soyle.stories.project.projectList.ProjectListView
import com.soyle.stories.workspace.ProjectAlreadyOpen
import com.soyle.stories.workspace.ProjectDoesNotExistAtLocation
import com.soyle.stories.workspace.ProjectException
import com.soyle.stories.workspace.UnexpectedProjectAlreadyOpenAtLocation
import com.soyle.stories.workspace.usecases.closeProject.CloseProject
import com.soyle.stories.workspace.usecases.openProject.OpenProject
import java.io.File

internal class OpenProjectPresenter(
  private val view: ProjectListView
) : ProjectOpenedReceiver {

    override suspend fun receiveOpenedProject(openedProject: OpenProject.ResponseModel) {
        if (openedProject.requiresConfirmation) {
            return view.updateOrInvalidated {
                copy(
                    openProjectRequest = ProjectFileViewModel(openedProject.projectId, openedProject.projectName, openedProject.projectLocation)
                )
            }
        }
        view.updateOrInvalidated {
            copy(
                isWelcomeScreenVisible = false,
                openProjectRequest = null,
                openProjects = openProjects + ProjectFileViewModel(openedProject.projectId, openedProject.projectName, openedProject.projectLocation)
            )
        }
    }
}