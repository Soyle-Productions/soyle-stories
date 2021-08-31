package com.soyle.stories.storyevent.time

import com.soyle.stories.domain.storyevent.events.StoryEventRescheduled

interface StoryEventRescheduledReceiver {
    suspend fun receiveStoryEventsRescheduled(events: List<StoryEventRescheduled>)
}