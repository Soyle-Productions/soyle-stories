package com.soyle.stories.project.closeProject

import com.soyle.stories.workspace.usecases.closeProject.CloseProject

class CloseProjectOutput(
    private val closedProjectReceiver: ClosedProjectReceiver
) : CloseProject.OutputPort {

    override suspend fun receiveCloseProjectResponse(response: CloseProject.ResponseModel) {
        closedProjectReceiver.receiveClosedProject(response)
    }

    override fun receiveCloseProjectFailure(failure: Exception) {
        throw failure
    }

}