package com.soyle.stories.project.projectList

import com.soyle.stories.workspace.ProjectAlreadyOpen
import com.soyle.stories.workspace.ProjectDoesNotExistAtLocation
import com.soyle.stories.workspace.ProjectException
import com.soyle.stories.workspace.UnexpectedProjectAlreadyOpenAtLocation
import com.soyle.stories.workspace.usecases.closeProject.CloseProject
import com.soyle.stories.workspace.usecases.openProject.OpenProject
import java.io.File

internal class OpenProjectPresenter(
    private val view: ProjectListView,
    private val closeProjectOutputPort: CloseProject.OutputPort
) : OpenProject.OutputPort {

    override fun receiveOpenProjectFailure(failure: ProjectException) {
        println(failure)
        view.updateOrInvalidated {
            copy(
                failedProjects = failedProjects + when (failure) {
                    is ProjectDoesNotExistAtLocation -> ProjectIssueViewModel(
                        File(failure.location).nameWithoutExtension,
                        failure.location
                    )
                    is UnexpectedProjectAlreadyOpenAtLocation -> ProjectIssueViewModel(
                        failure.foundProjectName,
                        failure.location,
                        "Different project already open at this location: ${failure.openProjectName}"
                    )
                    is ProjectAlreadyOpen -> ProjectIssueViewModel(failure.projectName, failure.location)
                    else -> ProjectIssueViewModel(
                        File(failure.location).nameWithoutExtension,
                        failure.location,
                        failure.localizedMessage
                    )
                }
            )
        }
    }

    override fun receiveOpenProjectResponse(response: OpenProject.ResponseModel) {
        if (response.requiresConfirmation) {
            return view.updateOrInvalidated {
                copy(
                    openProjectRequest = ProjectFileViewModel(response.projectId, response.projectName, response.projectLocation)
                )
            }
        }
        view.updateOrInvalidated {
            copy(
                isWelcomeScreenVisible = false,
                openProjectRequest = null,
                openProjects = openProjects + ProjectFileViewModel(response.projectId, response.projectName, response.projectLocation)
            )
        }
    }

    override fun receiveCloseProjectFailure(failure: Exception) {
        closeProjectOutputPort.receiveCloseProjectFailure(failure)
    }

    override fun receiveCloseProjectResponse(response: CloseProject.ResponseModel) {
        closeProjectOutputPort.receiveCloseProjectResponse(response)
    }
}