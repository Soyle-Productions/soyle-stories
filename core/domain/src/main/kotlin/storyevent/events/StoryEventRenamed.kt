package com.soyle.stories.domain.storyevent.events

import com.soyle.stories.domain.storyevent.StoryEvent

class StoryEventRenamed(override val storyEventId: StoryEvent.Id, val newName: String) : StoryEventChange()