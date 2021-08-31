package com.soyle.stories.storyevent.time.reschedule

import com.soyle.stories.domain.storyevent.events.StoryEventRescheduled

interface StoryEventRescheduledReceiver {
    suspend fun receiveStoryEventRescheduled(event: StoryEventRescheduled)
}