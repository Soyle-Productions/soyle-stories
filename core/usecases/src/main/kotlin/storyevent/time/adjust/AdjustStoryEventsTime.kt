package com.soyle.stories.usecase.storyevent.time.adjust

import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.storyevent.events.StoryEventRescheduled

interface AdjustStoryEventsTime {

    suspend operator fun invoke(storyEventIds: Set<StoryEvent.Id>, adjustment: Long, output: AdjustStoryEventsTime.OutputPort)

    class ResponseModel(
        val rescheduledStoryEvents: List<StoryEventRescheduled>
    )

    fun interface OutputPort {
        suspend fun adjustedTimesForStoryEvents(response: ResponseModel)
    }

}