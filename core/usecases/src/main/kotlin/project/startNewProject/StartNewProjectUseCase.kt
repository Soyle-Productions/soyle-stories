package com.soyle.stories.usecase.project.startNewProject

import arrow.core.Either
import com.soyle.stories.domain.project.Project
import com.soyle.stories.usecase.project.ProjectRepository

class StartNewProjectUseCase(
    private val projectRepository: ProjectRepository
) : StartNewProject {

    override suspend fun invoke(name: String, output: StartNewProject.OutputPort) {
        val projectResponse = Project.startNew(name)

        if (projectResponse !is Either.Right) {
            output.fail((projectResponse as Either.Left).a)
            return
        }

        projectRepository.addNewProject(projectResponse.b)
        output.receiveStartNewProjectResponse(StartNewProject.ResponseModel(projectResponse.b.id.uuid, name))
    }

    private fun StartNewProject.OutputPort.fail(failure: Throwable) {
        if (failure is Exception) {
            receiveStartNewProjectFailure(failure)
        } else {
            receiveStartNewProjectFailure(Exception(failure))
        }
    }
}