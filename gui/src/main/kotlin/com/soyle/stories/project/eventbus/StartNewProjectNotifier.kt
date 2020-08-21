package com.soyle.stories.project.eventbus

import com.soyle.stories.common.Notifier
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.project.LocalProjectException
import com.soyle.stories.project.usecases.startnewLocalProject.StartNewLocalProject
import com.soyle.stories.workspace.ProjectException
import com.soyle.stories.workspace.usecases.closeProject.CloseProject
import com.soyle.stories.workspace.usecases.openProject.OpenProject

class StartNewProjectNotifier(
    private val threadTransformer: ThreadTransformer,
    private val openProjectNotifier: OpenProjectNotifier
) : StartNewLocalProject.OutputPort, Notifier<StartNewLocalProject.OutputPort>() {

    override fun receiveOpenProjectFailure(failure: ProjectException) {
        openProjectNotifier.receiveOpenProjectFailure(failure)
    }

    override suspend fun receiveOpenProjectResponse(response: OpenProject.ResponseModel) {
        openProjectNotifier.receiveOpenProjectResponse(response)
    }

    override fun receiveStartNewLocalProjectFailure(exception: LocalProjectException) {
        threadTransformer.async {
            notifyAll { it.receiveStartNewLocalProjectFailure(exception) }
        }
    }

    override suspend fun receiveCloseProjectResponse(response: CloseProject.ResponseModel) {
        openProjectNotifier.receiveCloseProjectResponse(response)
    }

    override fun receiveCloseProjectFailure(failure: Exception) {
        openProjectNotifier.receiveCloseProjectFailure(failure)
    }

}