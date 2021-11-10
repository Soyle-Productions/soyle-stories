package com.soyle.stories.storyevent.time.reschedule

import com.soyle.stories.domain.storyevent.StoryEvent

interface RescheduleStoryEventPrompt : AutoCloseable {
    suspend fun requestNewTime(currentTime: Long): Long?
}