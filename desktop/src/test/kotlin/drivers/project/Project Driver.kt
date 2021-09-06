package com.soyle.stories.desktop.config.drivers.project

import com.soyle.stories.di.get
import com.soyle.stories.di.modules.DataModule
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.project.startNewProject.StartProjectController
import com.soyle.stories.soylestories.SoyleStories
import com.soyle.stories.workspace.repositories.WorkspaceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.runBlocking

class ProjectDriver(private val soyleStories: SoyleStories)
{

    fun givenProjectHasBeenStarted(): Project =
        getOpenProject() ?: openProject()

    fun getOpenProjectOrError(): Project =
        getOpenProject() ?: throw NoSuchElementException("No open projects in application")

    fun getOpenProject(): Project? {
        val workspace = soyleStories.scope.get<WorkspaceRepository>().run {
            runBlocking { getWorkSpaceForWorker(DataModule.workerId) }
        }
        return workspace?.openProjects?.firstOrNull()?.let { Project(it.projectId, NonBlankString.create(it.projectName)!!) }
    }

    fun openProject(): Project {
        val job = soyleStories.scope.get<StartProjectController>().startProject(
            "A Place", NonBlankString.create("Untitled")!!
        )
        runBlocking {
            job.join()
        }
        return getOpenProjectOrError()
    }
}