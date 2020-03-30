package com.soyle.stories.project.projectList

import com.soyle.stories.project.usecases.startnewLocalProject.StartNewLocalProject
import com.soyle.stories.workspace.usecases.closeProject.CloseProject
import com.soyle.stories.workspace.usecases.listOpenProjects.ListOpenProjects
import com.soyle.stories.workspace.usecases.openProject.OpenProject
import com.soyle.stories.workspace.usecases.requestCloseProject.RequestCloseProject
import java.net.URI
import java.util.*

/**
 * Created by Brendan
 * Date: 2/14/2020
 * Time: 3:44 PM
 */
class ProjectListController(
    private val listOpenProjects: ListOpenProjects,
    private val listOpenProjectsOutputPort: ListOpenProjects.OutputPort,
    private val closeProject: CloseProject,
    private val requestCloseProject: RequestCloseProject,
    private val requestCloseProjectOutputPort: RequestCloseProject.OutputPort,
    private val startNewProject: StartNewLocalProject,
    private val startNewProjectOutputPort: StartNewLocalProject.OutputPort,
    private val openProject: OpenProject,
    private val openProjectOutputPort: OpenProject.OutputPort
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
        startNewProject.invoke(
            StartNewLocalProject.RequestModel(directory, name),
            startNewProjectOutputPort
        )
    }

    override suspend fun openProject(location: String) {
        openProject.invoke(
            location,
            openProjectOutputPort
        )
    }

    override suspend fun forceOpenProject(location: String) {
        openProject.forceOpenProject(
            location,
            openProjectOutputPort
        )
    }

    override suspend fun replaceCurrentProject(location: String) {
        openProject.replaceOpenProject(
            location,
            openProjectOutputPort
        )
    }

}