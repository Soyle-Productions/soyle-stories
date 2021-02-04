/**
 * Created by Brendan
 * Date: 2/29/2020
 * Time: 11:03 AM
 */
package com.soyle.stories.project.usecases.startNewProject

import arrow.core.Either
import com.soyle.stories.entities.Project
import com.soyle.stories.project.repositories.ProjectRepository

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