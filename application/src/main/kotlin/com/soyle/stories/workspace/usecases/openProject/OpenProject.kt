/**
 * Created by Brendan
 * Date: 2/29/2020
 * Time: 6:14 PM
 */
package com.soyle.stories.workspace.usecases.openProject

import com.soyle.stories.workspace.ProjectException
import com.soyle.stories.workspace.usecases.closeProject.CloseProject
import java.util.*

interface OpenProject {

    suspend operator fun invoke(location: String, outputPort: OutputPort)
    suspend fun forceOpenProject(location: String, outputPort: OutputPort)
    suspend fun replaceOpenProject(location: String, outputPort: OutputPort)

    class ResponseModel(val projectId: UUID, val projectName: String, val projectLocation: String, val requiresConfirmation: Boolean)

    interface OutputPort : CloseProject.OutputPort {
        fun receiveOpenProjectFailure(failure: ProjectException)
        suspend fun receiveOpenProjectResponse(response: ResponseModel)
    }

}