package com.soyle.stories.storyevent.items

import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.usecase.storyevent.StoryEventItem

data class StoryEventListItemViewModel (
  val id: StoryEvent.Id,
  val name: String,
  val time: Long
) {
	constructor(item: StoryEventItem) : this(item.storyEventId, item.storyEventName, item.time)
}