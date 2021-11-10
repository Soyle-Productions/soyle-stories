package com.soyle.stories.storyevent.create

import com.soyle.stories.common.Notifier
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.storyevent.time.StoryEventRescheduledReceiver
import com.soyle.stories.usecase.storyevent.create.CreateStoryEvent

class CreateStoryEventOutput(
    private val storyEventCreatedReceiver: StoryEventCreatedReceiver,
    private val storyEventRescheduledReceiver: StoryEventRescheduledReceiver
) : CreateStoryEvent.OutputPort {
    override suspend fun receiveCreateStoryEventResponse(response: CreateStoryEvent.ResponseModel) {
        storyEventCreatedReceiver.receiveStoryEventCreated(response.createdStoryEvent)
        response.rescheduledStoryEvents?.run {
            storyEventRescheduledReceiver.receiveStoryEventsRescheduled(associateBy { it.storyEventId })
        }
    }

}