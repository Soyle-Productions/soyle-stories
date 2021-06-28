package com.soyle.stories.usecase.project.startNewProject

import com.soyle.stories.domain.validation.NonBlankString
import java.util.*

interface StartNewProject {
    suspend operator fun invoke(name: NonBlankString, output: OutputPort)

    class ResponseModel(val projectId: UUID, val projectName: String)

    interface OutputPort {
        fun receiveStartNewProjectFailure(failure: Throwable)
        suspend fun receiveStartNewProjectResponse(response: ResponseModel)
    }
}