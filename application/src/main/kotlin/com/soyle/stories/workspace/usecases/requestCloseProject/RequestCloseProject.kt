package com.soyle.stories.workspace.usecases.requestCloseProject

import com.soyle.stories.workspace.usecases.closeProject.CloseProject
import java.util.*

/**
 * Created by Brendan
 * Date: 2/16/2020
 * Time: 10:49 AM
 */
interface RequestCloseProject {

    suspend operator fun invoke(projectId: UUID, outputPort: OutputPort)

    class ResponseModel(val projectId: UUID, val projectName: String)

    interface OutputPort : CloseProject.OutputPort {
        fun receiveConfirmCloseProjectRequest(request: ResponseModel)
    }

}