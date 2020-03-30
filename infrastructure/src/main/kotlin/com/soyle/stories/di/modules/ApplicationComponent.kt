package com.soyle.stories.di.modules

import com.soyle.stories.project.eventbus.OpenProjectNotifier
import com.soyle.stories.project.eventbus.RequestCloseProjectNotifier
import com.soyle.stories.project.eventbus.StartNewProjectNotifier
import com.soyle.stories.project.projectList.ProjectListController
import com.soyle.stories.project.projectList.ProjectListPresenter
import com.soyle.stories.project.projectList.ProjectListViewListener
import com.soyle.stories.project.usecases.startNewProject.StartNewProjectUseCase
import com.soyle.stories.project.usecases.startnewLocalProject.StartNewLocalProjectUseCase
import com.soyle.stories.soylestories.ApplicationModel
import com.soyle.stories.workspace.usecases.closeProject.CloseProject
import com.soyle.stories.workspace.usecases.closeProject.CloseProjectUseCase
import com.soyle.stories.workspace.usecases.listOpenProjects.ListOpenProjects
import com.soyle.stories.workspace.usecases.listOpenProjects.ListOpenProjectsUseCase
import com.soyle.stories.workspace.usecases.openProject.OpenProject
import com.soyle.stories.workspace.usecases.openProject.OpenProjectUseCase
import com.soyle.stories.workspace.usecases.requestCloseProject.RequestCloseProject
import com.soyle.stories.workspace.usecases.requestCloseProject.RequestCloseProjectUseCase
import tornadofx.Component
import tornadofx.ScopedInstance

/**
 * Created by Brendan
 * Date: 2/14/2020
 * Time: 3:50 PM
 */
class ApplicationComponent : Component(), ScopedInstance {

    private val dataComponent: DataComponent by inject()

    val listOpenProjects: ListOpenProjects by lazy {
        ListOpenProjectsUseCase(
            dataComponent.workerId,
            dataComponent.workspaceRepository,
            dataComponent.projectFileRepository
        )
    }

    val closeProject: CloseProject by lazy {
        CloseProjectUseCase(dataComponent.workerId, dataComponent.workspaceRepository)
    }
    val requestCloseProject: RequestCloseProject by lazy {
        RequestCloseProjectUseCase(dataComponent.workerId, closeProject, dataComponent.workspaceRepository)
    }
    val openProject: OpenProject by lazy {
        OpenProjectUseCase(
            dataComponent.workerId,
            dataComponent.workspaceRepository,
            dataComponent.projectFileRepository,
            closeProject
        )
    }

    private val requestCloseProjectNotifier by lazy {
        RequestCloseProjectNotifier()
    }
    private val openProjectNotifier by lazy {
        OpenProjectNotifier(requestCloseProjectNotifier)
    }
    private val startNewProjectNotifier: StartNewProjectNotifier by lazy {
        StartNewProjectNotifier(openProjectNotifier)
    }

    val requestCloseProjectOutputPort: RequestCloseProject.OutputPort
        get() = requestCloseProjectNotifier

    val projectListViewListener: ProjectListViewListener by lazy {
        ProjectListController(
            listOpenProjects,
            ProjectListPresenter(
                find<ApplicationModel>(),
                openProjectNotifier,
                requestCloseProjectNotifier,
                startNewProjectNotifier
            ),
            closeProject,
            requestCloseProject,
            requestCloseProjectOutputPort,
            StartNewLocalProjectUseCase(
                dataComponent.fileRepository,
                StartNewProjectUseCase(
                    dataComponent.projectRepository
                ),
                openProject
            ),
            startNewProjectNotifier,
            openProject,
            openProjectNotifier
        )
    }

}