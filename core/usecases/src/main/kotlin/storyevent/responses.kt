package com.soyle.stories.usecase.storyevent

import com.soyle.stories.domain.storyevent.StoryEvent
import java.util.*

class StoryEventItem(val storyEventId: UUID, val storyEventName: String, val influenceOrderIndex: Int)

fun StoryEvent.toItem(influenceOrderIndex: Int) = StoryEventItem(id.uuid, name, influenceOrderIndex)