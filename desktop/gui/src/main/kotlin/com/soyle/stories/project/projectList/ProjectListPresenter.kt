package com.soyle.stories.project.projectList

import com.soyle.stories.project.closeProject.CloseProjectRequestReceiver
import com.soyle.stories.project.closeProject.ClosedProjectReceiver
import com.soyle.stories.project.openProject.ProjectOpenedReceiver
import com.soyle.stories.project.projectList.presenters.CloseProjectPresenter
import com.soyle.stories.project.projectList.presenters.OpenProjectPresenter
import com.soyle.stories.workspace.ExpectedProjectDoesNotExistAtLocation
import com.soyle.stories.workspace.ExpectedProjectException
import com.soyle.stories.workspace.UnexpectedProjectExistsAtLocation
import com.soyle.stories.workspace.usecases.listOpenProjects.ListOpenProjects

class ProjectListPresenter(
    private val view: ProjectListView
) : ListOpenProjects.OutputPort,
    ProjectOpenedReceiver by OpenProjectPresenter(view),
    ClosedProjectReceiver by CloseProjectPresenter(view),
    CloseProjectRequestReceiver by CloseProjectPresenter(view) {

    override suspend fun receiveListOpenProjectsResponse(response: ListOpenProjects.ResponseModel) {
        view.update {
            updateViewModelOrDefault(this, response)
        }
    }

    private fun updateViewModelOrDefault(
        viewModel: ProjectListViewModel?,
        response: ListOpenProjects.ResponseModel
    ): ProjectListViewModel {
        return if (viewModel != null) {
            updateViewModelWithResponse(viewModel, response)
        } else {
            defaultProjectListModel(response)
        }
    }

    private fun updateViewModelWithResponse(
        viewModel: ProjectListViewModel,
        response: ListOpenProjects.ResponseModel
    ): ProjectListViewModel {
        return viewModel.copy(
            isSplashScreenVisible = false,
            isWelcomeScreenVisible = response.openProjects.isEmpty() && response.failedProjects.isEmpty(),
            openProjects = response.openProjects.map(::convertOpenProjectItemToViewModel),
            failedProjects = response.failedProjects.map(::convertExpectedProjectExceptionToViewModel),
            startProjectFailure = null
        )
    }

    private fun defaultProjectListModel(response: ListOpenProjects.ResponseModel) = ProjectListViewModel(
        openProjectRequest = null,
        isSplashScreenVisible = false,
        isWelcomeScreenVisible = response.openProjects.isEmpty() && response.failedProjects.isEmpty(),
        openProjects = response.openProjects.map(::convertOpenProjectItemToViewModel),
        failedProjects = response.failedProjects.map(::convertExpectedProjectExceptionToViewModel),
        closeProjectRequest = null,
        startProjectFailure = null
    )

    private fun convertExpectedProjectExceptionToViewModel(it: ExpectedProjectException): ProjectIssueViewModel {
        return when (it) {
            is ExpectedProjectDoesNotExistAtLocation -> ProjectIssueViewModel(
                it.expectedProjectName,
                it.location
            )
            is UnexpectedProjectExistsAtLocation -> ProjectIssueViewModel(
                it.expectedProjectName,
                it.location,
                "Found unexpected project: ${it.foundProjectName}"
            )
        }
    }

    private fun convertOpenProjectItemToViewModel(item: ListOpenProjects.OpenProjectItem): ProjectFileViewModel {
        return ProjectFileViewModel(
            item.projectId,
            item.projectName,
            item.projectLocation
        )
    }


}