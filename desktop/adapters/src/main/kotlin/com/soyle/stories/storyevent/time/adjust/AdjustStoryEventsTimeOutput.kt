package com.soyle.stories.storyevent.time.adjust

import com.soyle.stories.storyevent.time.StoryEventRescheduledReceiver
import com.soyle.stories.usecase.storyevent.time.adjust.AdjustStoryEventsTime

class AdjustStoryEventsTimeOutput(
    private val storyEventRescheduledReceiver: StoryEventRescheduledReceiver
) : AdjustStoryEventsTime.OutputPort {

    override suspend fun adjustedTimesForStoryEvents(response: AdjustStoryEventsTime.ResponseModel) {
        storyEventRescheduledReceiver.receiveStoryEventsRescheduled(response.rescheduledStoryEvents)
    }
}