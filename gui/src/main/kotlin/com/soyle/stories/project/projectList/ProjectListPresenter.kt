package com.soyle.stories.project.projectList

import com.soyle.stories.eventbus.Notifier
import com.soyle.stories.project.usecases.startnewLocalProject.StartNewLocalProject
import com.soyle.stories.workspace.ExpectedProjectDoesNotExistAtLocation
import com.soyle.stories.workspace.UnexpectedProjectExistsAtLocation
import com.soyle.stories.workspace.usecases.listOpenProjects.ListOpenProjects
import com.soyle.stories.workspace.usecases.openProject.OpenProject
import com.soyle.stories.workspace.usecases.requestCloseProject.RequestCloseProject

class ProjectListPresenter(
    private val view: ProjectListView,
    openProjectNotifier: Notifier<OpenProject.OutputPort>,
    closeProjectNotifier: Notifier<RequestCloseProject.OutputPort>,
    startNewProjectNotifier: Notifier<StartNewLocalProject.OutputPort>
) : ListOpenProjects.OutputPort {

    private val closeProjectOutputPort: RequestCloseProject.OutputPort = CloseProjectPresenter(view)
    private val openProjectOutputPort: OpenProject.OutputPort = OpenProjectPresenter(view, closeProjectOutputPort)
    private val startNewLocalProjectOutputPort: StartNewLocalProject.OutputPort =
        StartNewLocalProjectPresenter(openProjectOutputPort, closeProjectOutputPort)

    init {
        openProjectNotifier.addListener(startNewLocalProjectOutputPort)
        closeProjectNotifier.addListener(closeProjectOutputPort)
        startNewProjectNotifier.addListener(startNewLocalProjectOutputPort)
        //resolveFailedProjectNotifier.addListener(this)
    }

    override fun receiveListOpenProjectsResponse(response: ListOpenProjects.ResponseModel) {
        view.update {
            this?.copy(
                isSplashScreenVisible = false,
                isWelcomeScreenVisible = response.openProjects.isEmpty() && response.failedProjects.isEmpty(),
                openProjects = response.openProjects.map {
                    ProjectFileViewModel(
                        it.projectId,
                        it.projectName,
                        it.projectLocation
                    )
                },
                failedProjects = response.failedProjects.map {
                    when (it) {
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
            )
                ?: ProjectListViewModel(
                    openProjectRequest = null,
                    isSplashScreenVisible = false,
                    isWelcomeScreenVisible = response.openProjects.isEmpty() && response.failedProjects.isEmpty(),
                    openProjects = response.openProjects.map {
                        ProjectFileViewModel(
                            it.projectId,
                            it.projectName,
                            it.projectLocation
                        )
                    },
                    failedProjects = response.failedProjects.map {
                        when (it) {
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
                    },
                    closeProjectRequest = null
                )
        }
    }
}