package com.soyle.stories.project.eventbus

import com.soyle.stories.common.Notifier
import com.soyle.stories.workspace.ProjectException
import com.soyle.stories.workspace.usecases.closeProject.CloseProject
import com.soyle.stories.workspace.usecases.openProject.OpenProject

/**
 * Created by Brendan
 * Date: 2/15/2020
 * Time: 6:23 PM
 */
class OpenProjectNotifier(
    private val closeProjectNotifier: RequestCloseProjectNotifier
) : OpenProject.OutputPort, Notifier<OpenProject.OutputPort>() {

    override fun receiveOpenProjectFailure(failure: ProjectException) {
        notifyAll {
            it.receiveOpenProjectFailure(failure)
        }
    }

    override fun receiveOpenProjectResponse(response: OpenProject.ResponseModel) {
        notifyAll {
            it.receiveOpenProjectResponse(response)
        }
    }

    override fun receiveCloseProjectFailure(failure: Exception) {
        closeProjectNotifier.receiveCloseProjectFailure(failure)
    }

    override fun receiveCloseProjectResponse(response: CloseProject.ResponseModel) {
        closeProjectNotifier.receiveCloseProjectResponse(response)
    }
}