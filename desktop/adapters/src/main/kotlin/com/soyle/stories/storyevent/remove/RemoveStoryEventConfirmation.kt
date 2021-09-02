package com.soyle.stories.storyevent.remove

import com.soyle.stories.domain.storyevent.StoryEvent

interface RemoveStoryEventConfirmation {
    fun requestDeleteStoryEventConfirmation(storyEventIds: Set<StoryEvent.Id>)
}