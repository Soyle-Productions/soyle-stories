package com.soyle.stories.project.list

import com.soyle.stories.workspace.usecases.listOpenProjects.ListOpenProjects
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

interface ListOpenProjectsController {

    fun listOpenProjects(output: ListOpenProjects.OutputPort): Job

    class Implementation(
        private val mainContext: CoroutineContext,
        private val asyncContext: CoroutineContext,

        private val listOpenProjects: ListOpenProjects
    ): ListOpenProjectsController, CoroutineScope by CoroutineScope(asyncContext) {

        override fun listOpenProjects(output: ListOpenProjects.OutputPort): Job {
            return launch {
                listOpenProjects.invoke {
                    withContext(mainContext) {
                        output.receiveListOpenProjectsResponse(it)
                    }
                }
            }
        }

    }

}