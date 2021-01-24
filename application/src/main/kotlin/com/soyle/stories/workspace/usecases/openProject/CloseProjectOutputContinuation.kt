package com.soyle.stories.workspace.usecases.openProject

import com.soyle.stories.workspace.usecases.closeProject.CloseProject
import kotlinx.coroutines.runBlocking

class CloseProjectOutputContinuation(
    private val openProject: OpenProject,
    private val outputPort: OpenProject.OutputPort,
    private val location: String
) : CloseProject.OutputPort {
    override fun receiveCloseProjectFailure(failure: java.lang.Exception) {
        outputPort.receiveCloseProjectFailure(failure)
    }

    override suspend fun receiveCloseProjectResponse(response: CloseProject.ResponseModel) {
        outputPort.receiveCloseProjectResponse(response)
        openProject.invoke(location, outputPort)
    }
}