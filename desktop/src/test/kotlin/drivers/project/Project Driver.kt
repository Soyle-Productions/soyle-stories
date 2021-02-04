package com.soyle.stories.desktop.config.drivers.project

import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.desktop.config.drivers.soylestories.getCreateButton
import com.soyle.stories.desktop.config.drivers.soylestories.getDirectoryInput
import com.soyle.stories.desktop.config.drivers.soylestories.getWelcomeScreenOrError
import com.soyle.stories.desktop.config.drivers.soylestories.givenStartProjectDialogHasBeenOpened
import com.soyle.stories.desktop.config.drivers.theme.ThemeDriver
import com.soyle.stories.di.get
import com.soyle.stories.di.modules.DataModule
import com.soyle.stories.di.scoped
import com.soyle.stories.entities.Project
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.projectList.ProjectListController
import com.soyle.stories.project.repositories.ProjectRepository
import com.soyle.stories.project.startNewProject.StartProjectController
import com.soyle.stories.project.startProjectDialog.StartProjectDialog
import com.soyle.stories.soylestories.ApplicationScope
import com.soyle.stories.soylestories.SoyleStories
import com.soyle.stories.workspace.repositories.WorkspaceRepository
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
        return workspace?.openProjects?.firstOrNull()?.let { Project(it.projectId, it.projectName) }
    }

    fun openProject(): Project {
        soyleStories.scope.get<StartProjectController>().startProject(
            "A Place", "Untitled"
        )
        return getOpenProjectOrError()
    }
}