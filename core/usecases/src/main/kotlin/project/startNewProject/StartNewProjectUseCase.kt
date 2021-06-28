package com.soyle.stories.usecase.project.startNewProject

import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.project.ProjectRepository

class StartNewProjectUseCase(
    private val projectRepository: ProjectRepository
) : StartNewProject {

    override suspend fun invoke(name: NonBlankString, output: StartNewProject.OutputPort) {
        val project = Project.startNew(name)

        projectRepository.addNewProject(project)
        output.receiveStartNewProjectResponse(StartNewProject.ResponseModel(project.id.uuid, name.value))
    }
}