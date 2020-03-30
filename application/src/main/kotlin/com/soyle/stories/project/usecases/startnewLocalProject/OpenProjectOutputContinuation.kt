package com.soyle.stories.project.usecases.startnewLocalProject

import com.soyle.stories.project.ProjectFailure
import com.soyle.stories.workspace.ProjectException
import com.soyle.stories.workspace.usecases.closeProject.CloseProject
import com.soyle.stories.workspace.usecases.openProject.OpenProject
import kotlin.coroutines.Continuation

internal class OpenProjectOutputContinuation(private val continuation: Continuation<OpenProject.ResponseModel>) : OpenProject.OutputPort {
    override fun receiveOpenProjectFailure(failure: ProjectException) {
        continuation.resumeWith(Result.failure(ProjectFailure(failure)))
    }
    override fun receiveOpenProjectResponse(response: OpenProject.ResponseModel) {
        continuation.resumeWith(Result.success(response))
    }

    override fun receiveCloseProjectResponse(response: CloseProject.ResponseModel) {
    }

    override fun receiveCloseProjectFailure(failure: Exception) {

    }
}