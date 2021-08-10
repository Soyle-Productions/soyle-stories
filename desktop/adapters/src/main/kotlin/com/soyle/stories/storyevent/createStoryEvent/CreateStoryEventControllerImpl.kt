package com.soyle.stories.storyevent.createStoryEvent

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.storyevent.create.CreateStoryEvent
import java.util.*

class CreateStoryEventControllerImpl(
    projectId: String,
    private val threadTransformer: ThreadTransformer,
    private val createStoryEvent: CreateStoryEvent,
    private val createStoryEventOutputPort: CreateStoryEvent.OutputPort
) : CreateStoryEventController {

    private val projectId = Project.Id(UUID.fromString(projectId))

    override fun createStoryEvent(name: NonBlankString) {
        createStoryEvent(CreateStoryEvent.RequestModel(name, projectId))
    }

    override fun createStoryEventBefore(name: NonBlankString, relativeStoryEventId: String) {
        val request = CreateStoryEvent.RequestModel(
            name, projectId,
            CreateStoryEvent.RequestModel.RequestedStoryEventTime.Relative(
                StoryEvent.Id(
                    UUID.fromString(
                        relativeStoryEventId
                    )
                ), -1
            )
        )
        createStoryEvent(request)
    }

    override fun createStoryEventAfter(name: NonBlankString, relativeStoryEventId: String) {
        val request = CreateStoryEvent.RequestModel(
            name, projectId,
            CreateStoryEvent.RequestModel.RequestedStoryEventTime.Relative(
                StoryEvent.Id(
                    UUID.fromString(
                        relativeStoryEventId
                    )
                ), +1
            )
        )
        createStoryEvent(request)
    }

    private fun createStoryEvent(request: CreateStoryEvent.RequestModel) {
        threadTransformer.async {
            createStoryEvent.invoke(request, createStoryEventOutputPort)
        }
    }
}