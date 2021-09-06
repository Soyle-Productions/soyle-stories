package com.soyle.stories.storyevent.time.reschedule

import com.soyle.stories.domain.storyevent.StoryEvent

interface RescheduleStoryEventPrompt {
    fun promptForNewTime(storyEventId: StoryEvent.Id, currentTime: Long)
}