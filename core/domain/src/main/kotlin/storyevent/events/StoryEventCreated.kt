package com.soyle.stories.domain.storyevent.events

import com.soyle.stories.domain.storyevent.StoryEvent

class StoryEventCreated(
    override val storyEventId: StoryEvent.Id,
    val name: String,
    val time: Long
) : StoryEventChange()