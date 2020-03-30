package com.soyle.stories.repositories

import com.soyle.stories.workspace.entities.Workspace
import com.soyle.stories.workspace.repositories.WorkspaceRepository

class WorkspaceRepositoryImpl : WorkspaceRepository {
    val workspaces = mutableMapOf<String, Workspace>()

    override suspend fun addNewWorkspace(workspace: Workspace) {
        workspaces[workspace.workerId] = workspace
    }

    override suspend fun getWorkSpaceForWorker(workerId: String): Workspace? = workspaces[workerId]

    override suspend fun updateWorkspace(workspace: Workspace) {
        workspaces[workspace.workerId] = workspace
    }
}