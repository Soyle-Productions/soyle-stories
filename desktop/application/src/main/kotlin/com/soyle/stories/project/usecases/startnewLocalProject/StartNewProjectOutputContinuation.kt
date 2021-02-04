package com.soyle.stories.project.usecases.startnewLocalProject

import com.soyle.stories.project.ProjectFailure
import com.soyle.stories.project.usecases.startNewProject.StartNewProject
import kotlin.coroutines.Continuation

internal class StartNewProjectOutputContinuation(private val continuation: Continuation<StartNewProject.ResponseModel>) : StartNewProject.OutputPort {
    override suspend fun receiveStartNewProjectResponse(response: StartNewProject.ResponseModel) {
        continuation.resumeWith(Result.success(response))
    }

    override fun receiveStartNewProjectFailure(failure: Throwable) {
        continuation.resumeWith(Result.failure(ProjectFailure(failure)))
    }
}