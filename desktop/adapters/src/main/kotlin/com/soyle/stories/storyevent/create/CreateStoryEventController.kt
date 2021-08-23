package com.soyle.stories.storyevent.create

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.storyevent.create.CreateStoryEvent
import kotlinx.coroutines.Job
import java.util.*

interface CreateStoryEventController {

    companion object {

        operator fun invoke(
            projectId: Project.Id,
            threadTransformer: ThreadTransformer,
            createStoryEvent: CreateStoryEvent,
            createStoryEventOutputPort: CreateStoryEvent.OutputPort
        ): CreateStoryEventController = object : CreateStoryEventController {

            override fun createStoryEvent(name: NonBlankString): Job {
                return createStoryEvent(CreateStoryEvent.RequestModel(name, projectId))
            }

            override fun createStoryEvent(name: NonBlankString, timeUnit: Long): Job {
                return createStoryEvent(
                    CreateStoryEvent.RequestModel(
                        name,
                        projectId,
                        CreateStoryEvent.RequestModel.RequestedStoryEventTime.Absolute(timeUnit)
                    )
                )
            }

            override fun createStoryEvent(name: NonBlankString, relativeTo: CreateStoryEvent.RequestModel.RequestedStoryEventTime.Relative): Job {
                val request = CreateStoryEvent.RequestModel(
                    name, projectId,
                    relativeTo
                )
                return createStoryEvent(request)
            }

            private fun createStoryEvent(request: CreateStoryEvent.RequestModel): Job {
                return threadTransformer.async {
                    createStoryEvent.invoke(request, createStoryEventOutputPort)
                }
            }
        }
    }

    fun createStoryEvent(name: NonBlankString): Job

    fun createStoryEvent(name: NonBlankString, timeUnit: Long): Job

    fun createStoryEvent(name: NonBlankString, relativeTo: CreateStoryEvent.RequestModel.RequestedStoryEventTime.Relative): Job

}