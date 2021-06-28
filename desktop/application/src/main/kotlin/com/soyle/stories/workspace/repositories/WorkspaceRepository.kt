/**
 * Created by Brendan
 * Date: 3/1/2020
 * Time: 9:22 AM
 */
package com.soyle.stories.workspace.repositories

import com.soyle.stories.workspace.entities.Workspace

interface WorkspaceRepository {
    suspend fun getWorkSpaceForWorker(workerId: String): Workspace?
    suspend fun updateWorkspace(workspace: Workspace)
    suspend fun addNewWorkspace(workspace: Workspace)
}