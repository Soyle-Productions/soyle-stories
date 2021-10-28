package com.soyle.stories.storyevent.time.reschedule

import com.soyle.stories.storyevent.time.StoryEventRescheduledReceiver
import com.soyle.stories.usecase.storyevent.time.reschedule.RescheduleStoryEvent

class RescheduleStoryEventOutput(
    private val storyEventRescheduledReceiver: StoryEventRescheduledReceiver
) : RescheduleStoryEvent.OutputPort {

    override suspend fun storyEventRescheduled(response: RescheduleStoryEvent.ResponseModel) {
        storyEventRescheduledReceiver.receiveStoryEventsRescheduled(
            listOf(response.storyEventRescheduled).associateBy { it.storyEventId }
        )
    }
}