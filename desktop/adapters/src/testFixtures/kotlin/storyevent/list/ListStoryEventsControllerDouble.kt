package com.soyle.stories.desktop.adapter.storyevent.list

import com.soyle.stories.domain.project.Project
import com.soyle.stories.storyevent.list.ListStoryEventsController
import com.soyle.stories.usecase.storyevent.listAllStoryEvents.ListAllStoryEvents
import kotlinx.coroutines.*

class ListStoryEventsControllerDouble(
    private val onListStoryEventsInProject: (Project.Id, ListAllStoryEvents.OutputPort) -> Unit = { _, _ ->  },
    private val exceptionHandler: CoroutineExceptionHandler = CoroutineExceptionHandler { context, it -> context.cancel(CancellationException("", it)) }
) : ListStoryEventsController {

    var requestedProjectId: Project.Id? = null
        private set

    var shouldFailWith: Throwable? = null
    var failAfterResult = false

    fun failWhenCalled(with: Throwable = Error("Intentional Failure")) {
        shouldFailWith = with
    }


    override fun listStoryEventsInProject(projectId: Project.Id, output: ListAllStoryEvents.OutputPort): Job {
        requestedProjectId = projectId
        val failure = shouldFailWith
        return CoroutineScope(Dispatchers.Default).launch(exceptionHandler) {
            if (failure != null) {
                if (! failAfterResult) throw failure
                onListStoryEventsInProject(projectId, output)
                if (failAfterResult) throw failure
            } else {
                onListStoryEventsInProject(projectId, output)
            }
        }
    }
}