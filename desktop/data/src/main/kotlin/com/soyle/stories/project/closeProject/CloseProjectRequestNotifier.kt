package com.soyle.stories.project.closeProject

import com.soyle.stories.common.Notifier
import com.soyle.stories.workspace.usecases.requestCloseProject.RequestCloseProject

class CloseProjectRequestNotifier : CloseProjectRequestReceiver, Notifier<CloseProjectRequestReceiver>() {
    override suspend fun receiveCloseProjectRequest(event: RequestCloseProject.ResponseModel) {
        notifyAll { it.receiveCloseProjectRequest(event) }
    }
}