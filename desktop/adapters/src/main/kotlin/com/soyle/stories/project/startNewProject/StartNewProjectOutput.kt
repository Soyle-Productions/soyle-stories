package com.soyle.stories.project.startNewProject

import com.soyle.stories.project.openProject.OpenProjectController
import com.soyle.stories.usecase.project.startNewProject.StartNewProject

class StartNewProjectOutput(
    private val projectStartedReceiver: ProjectStartedReceiver,
    private val openProjectController: OpenProjectController
) : StartNewProject.OutputPort {

    override suspend fun receiveStartNewProjectResponse(response: StartNewProject.ResponseModel) {
        projectStartedReceiver.receiveProjectStarted(response)
    }

    override fun receiveStartNewProjectFailure(failure: Throwable) {
        throw failure
    }

}