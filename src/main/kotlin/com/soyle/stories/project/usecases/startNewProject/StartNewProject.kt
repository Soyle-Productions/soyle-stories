/**
 * Created by Brendan
 * Date: 2/29/2020
 * Time: 11:03 AM
 */
package com.soyle.stories.project.usecases.startNewProject

import java.util.*

interface StartNewProject {
    suspend operator fun invoke(name: String, output: OutputPort)

    class ResponseModel(val projectId: UUID, val projectName: String)

    interface OutputPort {
        fun receiveStartNewProjectFailure(failure: Throwable)
        fun receiveStartNewProjectResponse(response: ResponseModel)
    }
}