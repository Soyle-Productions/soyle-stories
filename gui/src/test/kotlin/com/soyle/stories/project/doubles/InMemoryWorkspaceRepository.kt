package com.soyle.stories.project.doubles

import com.soyle.stories.workspace.entities.Workspace
import com.soyle.stories.workspace.repositories.WorkspaceRepository

class InMemoryWorkspaceRepository: WorkspaceRepository {

    val workspaces = mutableMapOf<String, Workspace>()

    override suspend fun addNewWorkspace(workspace: Workspace) {
        workspaces[workspace.workerId] = workspace
    }

    override suspend fun getWorkSpaceForWorker(workerId: String): Workspace? {
        return workspaces[workerId].also {
            //println("getWorkSpaceForWorker($workerId) -> Workspace(${it?.openProjects})")
        }
    }

    override suspend fun updateWorkspace(workspace: Workspace) {
        workspaces[workspace.workerId] = workspace
    }


}