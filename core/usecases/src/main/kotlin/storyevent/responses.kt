package com.soyle.stories.usecase.storyevent

import com.soyle.stories.domain.storyevent.StoryEvent
import java.util.*

class StoryEventItem(val storyEventId: StoryEvent.Id, val storyEventName: String, val time: Long)

fun StoryEvent.toItem() = StoryEventItem(id, name.value, time)