package com.soyle.stories.project.closeProject

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.workspace.usecases.closeProject.CloseProject
import com.soyle.stories.workspace.usecases.requestCloseProject.RequestCloseProject

class CloseProjectOutput(
    private val threadTransformer: ThreadTransformer,
    private val closedProjectReceiver: ClosedProjectReceiver,
    private val closeProjectRequestReceiver: CloseProjectRequestReceiver
) : CloseProject.OutputPort,
    RequestCloseProject.OutputPort {

    override suspend fun receiveCloseProjectResponse(response: CloseProject.ResponseModel) {
        closedProjectReceiver.receiveClosedProject(response)
    }

    override fun receiveCloseProjectFailure(failure: Exception) {
        throw failure
    }

    override fun receiveConfirmCloseProjectRequest(request: RequestCloseProject.ResponseModel) {
        threadTransformer.async {
            closeProjectRequestReceiver.receiveCloseProjectRequest(request)
        }
    }

}