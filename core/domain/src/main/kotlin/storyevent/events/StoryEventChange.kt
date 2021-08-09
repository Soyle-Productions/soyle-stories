package com.soyle.stories.domain.storyevent.events

import com.soyle.stories.domain.storyevent.StoryEvent

abstract class StoryEventChange {
    abstract val storyEventId: StoryEvent.Id
}