package com.soyle.stories.storyevent.list

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.project.Project
import com.soyle.stories.usecase.storyevent.listAllStoryEvents.ListAllStoryEvents
import kotlinx.coroutines.Job

interface ListStoryEventsController {

    companion object {
        operator fun invoke(
            threadTransformer: ThreadTransformer,
            listAllStoryEvents: ListAllStoryEvents
        ): ListStoryEventsController = object : ListStoryEventsController {
            override fun listStoryEventsInProject(projectId: Project.Id, output: ListAllStoryEvents.OutputPort): Job {
                return threadTransformer.async {
                    listAllStoryEvents(projectId.uuid, output)
                }
            }
        }
    }

    fun listStoryEventsInProject(projectId: Project.Id, output: ListAllStoryEvents.OutputPort): Job

}