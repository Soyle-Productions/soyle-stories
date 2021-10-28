package com.soyle.stories.storyevent.time

import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.storyevent.events.StoryEventRescheduled

interface StoryEventRescheduledReceiver {
    suspend fun receiveStoryEventsRescheduled(events: Map<StoryEvent.Id, StoryEventRescheduled>)
}