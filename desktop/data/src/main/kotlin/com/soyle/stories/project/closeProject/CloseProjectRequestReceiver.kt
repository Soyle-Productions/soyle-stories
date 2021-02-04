package com.soyle.stories.project.closeProject

import com.soyle.stories.workspace.usecases.requestCloseProject.RequestCloseProject

interface CloseProjectRequestReceiver {

    suspend fun receiveCloseProjectRequest(event: RequestCloseProject.ResponseModel)

}