package com.soyle.stories.project.projectList

import com.soyle.stories.project.openProject.OpenProjectController
import com.soyle.stories.project.startNewProject.StartProjectController
import com.soyle.stories.project.usecases.startNewProject.StartNewProject
import com.soyle.stories.project.usecases.startNewProject.StartNewProjectUseCase
import com.soyle.stories.project.usecases.startnewLocalProject.StartNewLocalProject
import com.soyle.stories.repositories.ProjectRepositoryImpl
import com.soyle.stories.stores.FileStore
import com.soyle.stories.workspace.usecases.closeProject.CloseProject
import com.soyle.stories.workspace.usecases.listOpenProjects.ListOpenProjects
import com.soyle.stories.workspace.usecases.openProject.OpenProject
import com.soyle.stories.workspace.usecases.requestCloseProject.RequestCloseProject
import com.soyle.stories.workspace.valueobjects.ProjectFile
import java.net.URI
import java.util.*

class ProjectListController(
    private val listOpenProjects: ListOpenProjects,
    private val listOpenProjectsOutputPort: ListOpenProjects.OutputPort,
    private val closeProject: CloseProject,
    private val requestCloseProject: RequestCloseProject,
    private val requestCloseProjectOutputPort: RequestCloseProject.OutputPort,
    private val startProjectController: StartProjectController,
    private val openProjectController: OpenProjectController
) : ProjectListViewListener {

    override suspend fun startApplicationWithParameters(parameters: List<String>) {
        listOpenProjects.invoke(listOpenProjectsOutputPort)
    }

    override suspend fun closeProject(projectId: UUID) {
        closeProject.invoke(projectId, requestCloseProjectOutputPort)
    }

    override suspend fun requestCloseProject(projectId: UUID) {
        requestCloseProject.invoke(projectId, requestCloseProjectOutputPort)
    }

    override suspend fun ignoreFailure(failingURI: URI) {
        // resolveFailedProject.ignore(System.getProperty("user.name") ?: "", failingURI, resolveFailedProjectOutputPort)
    }

    override suspend fun startNewProject(directory: String, name: String) {
        startProjectController.startProject(directory, name)
    }

    override fun openProject(location: String) {
        openProjectController.openProject(location)
    }

    override fun forceOpenProject(location: String) {
        openProjectController.forceOpenProject(location)
    }

    override fun replaceCurrentProject(location: String) {
        openProjectController.replaceOpenProject(location)
    }

}