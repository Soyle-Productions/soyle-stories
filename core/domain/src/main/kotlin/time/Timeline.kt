package com.soyle.stories.domain.time

import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.time.changes.TimelineUpdate
import com.soyle.stories.domain.time.changes.UnSuccessful

class Timeline {
    fun withEvent(eventId: StoryEvent.Id): TimelineUpdate<*> {
        return UnSuccessful(this)
    }
}