package com.soyle.stories.workspace.usecases.closeProject

import java.util.*

/**
 * Created by Brendan
 * Date: 2/13/2020
 * Time: 10:01 PM
 */
interface CloseProject {

    suspend operator fun invoke(projectId: UUID, outputPort: OutputPort)

    class ResponseModel(val projectId: UUID)

    interface OutputPort {
        fun receiveCloseProjectFailure(failure: Exception)
        suspend fun receiveCloseProjectResponse(response: ResponseModel)
    }

}