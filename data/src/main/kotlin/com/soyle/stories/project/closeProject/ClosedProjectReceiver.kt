package com.soyle.stories.project.closeProject

import com.soyle.stories.workspace.usecases.closeProject.CloseProject

interface ClosedProjectReceiver {
    suspend fun receiveClosedProject(closedProject: CloseProject.ResponseModel)
}