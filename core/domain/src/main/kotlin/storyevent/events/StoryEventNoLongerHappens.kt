package com.soyle.stories.domain.storyevent.events

import com.soyle.stories.domain.storyevent.StoryEvent

data class StoryEventNoLongerHappens(override val storyEventId: StoryEvent.Id) : StoryEventChange()