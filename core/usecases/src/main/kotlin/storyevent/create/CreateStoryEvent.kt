package com.soyle.stories.usecase.storyevent.create

import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.storyevent.events.StoryEventCreated
import com.soyle.stories.domain.storyevent.events.StoryEventRescheduled
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.storyevent.StoryEventItem
import java.util.*

interface CreateStoryEvent {

    class RequestModel(
        val name: NonBlankString,
        val projectId: Project.Id,
        val time: RequestedStoryEventTime? = null
    ) {

        constructor(name: NonBlankString, projectId: Project.Id, time: Long) : this(
            name,
            projectId,
            RequestedStoryEventTime.Absolute(time)
        )

        constructor(
            name: NonBlankString,
            projectId: Project.Id,
            relativeStoryEvent: StoryEvent.Id,
            delta: Long
        ) : this(
            name,
            projectId,
            RequestedStoryEventTime.Relative(relativeStoryEvent, delta)
        )

        sealed class RequestedStoryEventTime {

            class Absolute(val time: Long) : RequestedStoryEventTime()
            class Relative(val relativeStoryEventId: StoryEvent.Id, val delta: Long) : RequestedStoryEventTime()
        }


    }

    suspend operator fun invoke(request: RequestModel, output: OutputPort)

    class ResponseModel(
        val createdStoryEvent: StoryEventCreated,
        val rescheduledStoryEvents: List<StoryEventRescheduled>?
    )

    fun interface OutputPort {

        suspend fun receiveCreateStoryEventResponse(response: ResponseModel)
    }

}