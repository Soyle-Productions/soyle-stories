package com.soyle.stories.domain.storyevent.events

import com.soyle.stories.domain.storyevent.StoryEvent

data class StoryEventRescheduled(override val storyEventId: StoryEvent.Id, val newTime: ULong, val originalTime: ULong) : StoryEventChange()