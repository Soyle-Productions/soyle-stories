package com.soyle.stories.storyevent.usecases

import com.soyle.stories.entities.StoryEvent
import java.util.*

class StoryEventItem(val storyEventId: UUID, val storyEventName: String, val influenceOrderIndex: Int)

fun StoryEvent.toItem(influenceOrderIndex: Int) = StoryEventItem(id.uuid, name, influenceOrderIndex)