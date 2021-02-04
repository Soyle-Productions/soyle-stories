package com.soyle.stories.project.openProject

import com.soyle.stories.project.closeProject.ClosedProjectReceiver
import com.soyle.stories.workspace.ProjectException
import com.soyle.stories.workspace.usecases.closeProject.CloseProject
import com.soyle.stories.workspace.usecases.openProject.OpenProject

class OpenProjectOutput(
    private val openedProjectReceiver: ProjectOpenedReceiver,
    private val closedProjectReceiver: ClosedProjectReceiver
) : OpenProject.OutputPort {

    override suspend fun receiveOpenProjectResponse(response: OpenProject.ResponseModel) {
        openedProjectReceiver.receiveOpenedProject(response)
    }

    override suspend fun receiveCloseProjectResponse(response: CloseProject.ResponseModel) {
        closedProjectReceiver.receiveClosedProject(response)
    }

    override fun receiveCloseProjectFailure(failure: Exception) {
        throw failure
    }

    override fun receiveOpenProjectFailure(failure: ProjectException) {
        throw failure
    }
}