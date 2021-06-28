/**
 * Created by Brendan
 * Date: 2/29/2020
 * Time: 12:03 PM
 */
package com.soyle.stories.workspace.usecases.listOpenProjects

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.soyle.stories.workspace.ExpectedProjectDoesNotExistAtLocation
import com.soyle.stories.workspace.UnexpectedProjectExistsAtLocation
import com.soyle.stories.workspace.repositories.ProjectRepository
import com.soyle.stories.workspace.repositories.WorkspaceRepository

class ListOpenProjectsUseCase(
    val workerId: String,
    val workspaceRepository: WorkspaceRepository,
    val projectRepository: ProjectRepository
) : ListOpenProjects {

    override suspend fun invoke(output: ListOpenProjects.OutputPort) {
        val workspace = workspaceRepository.getWorkSpaceForWorker(workerId)
            ?: return output.receiveListOpenProjectsResponse(ListOpenProjects.ResponseModel(emptyList(), emptyList()))

        val projects = workspace.openProjects
            .associateWith { projectRepository.getProjectAtLocation(it.location) }
            .entries
            .map { (file, project) ->
                when {
                    project == null -> ExpectedProjectDoesNotExistAtLocation(
                        file.projectId.uuid,
                        file.projectName,
                        file.location
                    ).left()
                    file.projectId != project.id -> UnexpectedProjectExistsAtLocation(
                        project.id.uuid,
                        project.name.value,
                        file.projectId.uuid,
                        file.projectName,
                        file.location
                    ).left()
                    else -> ListOpenProjects.OpenProjectItem(project.id.uuid, project.name.value, file.location).right()
                }
            }

        output.receiveListOpenProjectsResponse(
            ListOpenProjects.ResponseModel(
                projects.filter { it.isRight() }.map { (it as Either.Right).b },
                projects.filter { it.isLeft() }.map { (it as Either.Left).a }
            )
        )

    }

}