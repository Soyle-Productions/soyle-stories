package com.soyle.stories.storyevent.time.reschedule

import com.soyle.stories.common.Notifier
import com.soyle.stories.usecase.storyevent.time.reschedule.RescheduleStoryEvent

class RescheduleStoryEventOutput(
    private val storyEventRescheduledReceiver: StoryEventRescheduledReceiver
) : RescheduleStoryEvent.OutputPort {

    override suspend fun storyEventRescheduled(response: RescheduleStoryEvent.ResponseModel) {
        storyEventRescheduledReceiver.receiveStoryEventRescheduled(response.storyEventRescheduled)
    }
}