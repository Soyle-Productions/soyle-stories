package com.soyle.stories.project.eventbus

import com.soyle.stories.common.Notifier
import com.soyle.stories.workspace.usecases.closeProject.CloseProject
import com.soyle.stories.workspace.usecases.requestCloseProject.RequestCloseProject

/**
 * Created by Brendan
 * Date: 2/15/2020
 * Time: 6:25 PM
 */
class RequestCloseProjectNotifier : RequestCloseProject.OutputPort, Notifier<RequestCloseProject.OutputPort>() {

    override fun receiveCloseProjectFailure(failure: Exception) {
        notifyAll {
            it.receiveCloseProjectFailure(failure)
        }
    }

    override fun receiveCloseProjectResponse(response: CloseProject.ResponseModel) {
        notifyAll {
            it.receiveCloseProjectResponse(response)
        }
    }

    override fun receiveConfirmCloseProjectRequest(request: RequestCloseProject.ResponseModel) {
        notifyAll {
            it.receiveConfirmCloseProjectRequest(request)
        }
    }

}