package com.soyle.stories.workspace.usecases.closeProject

import arrow.core.Either
import com.soyle.stories.workspace.ProjectNotOpen
import com.soyle.stories.workspace.repositories.WorkspaceRepository
import java.util.*

/**
 * Created by Brendan
 * Date: 2/13/2020
 * Time: 10:02 PM
 */
class CloseProjectUseCase(
    private val workerId: String,
    private val workSpaceRepository: WorkspaceRepository
) : CloseProject {

    override suspend fun invoke(projectId: UUID, outputPort: CloseProject.OutputPort) {
        val workspace = workSpaceRepository.getWorkSpaceForWorker(workerId)
        if (workspace == null) {
            outputPort.receiveCloseProjectFailure(ProjectNotOpen(projectId))
            return
        }

        val openProjectWithId = workspace.openProjects.find { it.projectId.uuid == projectId }
        if (openProjectWithId == null) {
            outputPort.receiveCloseProjectFailure(ProjectNotOpen(projectId))
            return
        }

        val removeProjectResponse = workspace.removeProject(openProjectWithId.projectId)

        if (removeProjectResponse !is Either.Right) {
            return outputPort.receiveCloseProjectFailure((removeProjectResponse as Either.Left).a)
        }

        workSpaceRepository.updateWorkspace(removeProjectResponse.b)

        outputPort.receiveCloseProjectResponse(CloseProject.ResponseModel(projectId))

        /*

        workSpace.closeProject(Project.Id(projectId))
            .fold(
                {
                    outputPort.receiveCloseProjectFailure(it)
                },
                {
                    workSpaceRepository.saveWorkspace(it)
                    outputPort.receiveCloseProjectResponse(it.events)
                }
            )
*/
    }

}