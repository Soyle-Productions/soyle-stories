package com.soyle.stories.usecase.storyevent.create

import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.storyevent.events.StoryEventCoveredByScene
import com.soyle.stories.domain.storyevent.events.StoryEventCreated
import com.soyle.stories.domain.storyevent.events.StoryEventRescheduled
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.storyevent.StoryEventItem
import java.util.*

interface CreateStoryEvent {

    class RequestModel(
        val name: NonBlankString,
        val projectId: Project.Id,
        val sceneId: Scene.Id? = null,
        val time: RequestedStoryEventTime? = null
    ) {

        constructor(name: NonBlankString, projectId: Project.Id, time: Long, sceneId: Scene.Id? = null) : this(
            name,
            projectId,
            sceneId,
            RequestedStoryEventTime.Absolute(time)
        )

        constructor(
            name: NonBlankString,
            projectId: Project.Id,
            relativeStoryEvent: StoryEvent.Id,
            delta: Long,
            sceneId: Scene.Id? = null
        ) : this(
            name,
            projectId,
            sceneId,
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
        val storyEventCovered: StoryEventCoveredByScene?,
        val rescheduledStoryEvents: List<StoryEventRescheduled>?
    )

    fun interface OutputPort {

        suspend fun receiveCreateStoryEventResponse(response: ResponseModel)
    }

}