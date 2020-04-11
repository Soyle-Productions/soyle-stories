package com.soyle.stories.project.projectList.presenters

import com.soyle.stories.project.projectList.ProjectListView
import com.soyle.stories.project.projectList.ProjectViewModel
import com.soyle.stories.workspace.usecases.closeProject.CloseProject
import com.soyle.stories.workspace.usecases.requestCloseProject.RequestCloseProject

internal class CloseProjectPresenter(
    private val view: ProjectListView
) : RequestCloseProject.OutputPort {

    override fun receiveCloseProjectFailure(failure: Exception) {
        println(failure)
    }

    override fun receiveCloseProjectResponse(response: CloseProject.ResponseModel) {

        view.updateOrInvalidated {
            val openProjectList = openProjects.filterNot { it.projectId == response.projectId }
            copy(
                isWelcomeScreenVisible = openProjectList.isEmpty(),
                openProjects = openProjectList,
                closeProjectRequest = null
            )
        }
    }

    override fun receiveConfirmCloseProjectRequest(request: RequestCloseProject.ResponseModel) {
        view.updateOrInvalidated {
            copy(
                closeProjectRequest = ProjectViewModel(request.projectId, request.projectName)
            )
        }
    }

}