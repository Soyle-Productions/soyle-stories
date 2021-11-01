package com.soyle.stories.domain.time

import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.time.changes.Successful
import com.soyle.stories.domain.time.changes.TimelineUpdate
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class `Timeline Unit Test` {

    val timeline = Timeline()

    @Test
    fun `add event to a point in time`() {
        val (changedTimeline, change) = timeline.withEvent(StoryEvent.Id()) as Successful
    }

}