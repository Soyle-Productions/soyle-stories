package com.soyle.stories.project.usecases.startnewLocalProject

import com.soyle.stories.project.LocalProjectException
import com.soyle.stories.workspace.usecases.openProject.OpenProject
import java.util.*

interface StartNewLocalProject {

    class RequestModel(val directory: String, val projectName: String)

    suspend operator fun invoke(request: RequestModel, output: OutputPort)

    class ResponseModel(val projectId: UUID, val projectName: String, val fileLocation: String)

    interface OutputPort : OpenProject.OutputPort {
        fun receiveStartNewLocalProjectFailure(exception: LocalProjectException)
    }
}