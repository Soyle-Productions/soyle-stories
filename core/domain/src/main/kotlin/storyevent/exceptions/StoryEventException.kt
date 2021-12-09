package com.soyle.stories.domain.storyevent.exceptions

import com.soyle.stories.domain.storyevent.StoryEvent

interface StoryEventException {
    val storyEventId: StoryEvent.Id
}