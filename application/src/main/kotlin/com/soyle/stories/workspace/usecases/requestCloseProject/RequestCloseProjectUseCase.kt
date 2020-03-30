package com.soyle.stories.workspace.usecases.requestCloseProject

import com.soyle.stories.workspace.ProjectNotOpen
import com.soyle.stories.workspace.repositories.WorkspaceRepository
import com.soyle.stories.workspace.usecases.closeProject.CloseProject
import java.util.*

/**
 * Created by Brendan
 * Date: 2/16/2020
 * Time: 10:50 AM
 */
class RequestCloseProjectUseCase(
    private val workerId: String,
    private val closeProject: CloseProject,
    private val workSpaceRepository: WorkspaceRepository
) : RequestCloseProject {

    override suspend fun invoke(projectId: UUID, outputPort: RequestCloseProject.OutputPort) {
        val workspace = workSpaceRepository.getWorkSpaceForWorker(workerId)
            ?: return outputPort.receiveCloseProjectFailure(ProjectNotOpen(projectId))
        if (workspace.openProjects.size == 1) {
            val project = workspace.openProjects.find { it.projectId.uuid == projectId }
            if (project?.projectId?.uuid != projectId) {
                return outputPort.receiveCloseProjectFailure(ProjectNotOpen(projectId))
            }
            outputPort.receiveConfirmCloseProjectRequest(RequestCloseProject.ResponseModel(project.projectId.uuid, project.projectName))
        }
        else closeProject(projectId, outputPort)
    }

}