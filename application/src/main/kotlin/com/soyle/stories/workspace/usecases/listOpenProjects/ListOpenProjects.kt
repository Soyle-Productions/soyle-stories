/**
 * Created by Brendan
 * Date: 2/29/2020
 * Time: 12:03 PM
 */
package com.soyle.stories.workspace.usecases.listOpenProjects

import com.soyle.stories.workspace.ExpectedProjectException
import java.util.*

interface ListOpenProjects {

    suspend operator fun invoke(output: OutputPort)

    class ResponseModel(val openProjects: List<OpenProjectItem>, val failedProjects: List<ExpectedProjectException>)

    class OpenProjectItem(val projectId: UUID, val projectName: String, val projectLocation: String)

    interface OutputPort {
        fun receiveListOpenProjectsResponse(response: ResponseModel)
    }

}