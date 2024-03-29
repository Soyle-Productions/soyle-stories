package com.soyle.stories.project.projectList.presenters

import com.soyle.stories.project.closeProject.CloseProjectRequestReceiver
import com.soyle.stories.project.closeProject.ClosedProjectReceiver
import com.soyle.stories.project.projectList.ProjectListView
import com.soyle.stories.project.projectList.ProjectViewModel
import com.soyle.stories.workspace.usecases.closeProject.CloseProject
import com.soyle.stories.workspace.usecases.requestCloseProject.RequestCloseProject

internal class CloseProjectPresenter(
    private val view: ProjectListView
) : ClosedProjectReceiver, CloseProjectRequestReceiver {

    override suspend fun receiveClosedProject(closedProject: CloseProject.ResponseModel) {
        view.updateOrInvalidated {
            val openProjectList = openProjects.filterNot { it.projectId == closedProject.projectId }
            copy(
                isWelcomeScreenVisible = openProjectList.isEmpty(),
                openProjects = openProjectList,
                closeProjectRequest = null
            )
        }
    }

    override suspend fun receiveCloseProjectRequest(event: RequestCloseProject.ResponseModel) {
        view.updateOrInvalidated {
            copy(
                closeProjectRequest = ProjectViewModel(event.projectId, event.projectName)
            )
        }
    }

}