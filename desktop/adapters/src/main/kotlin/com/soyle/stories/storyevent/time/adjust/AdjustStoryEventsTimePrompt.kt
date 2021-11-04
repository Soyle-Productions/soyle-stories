package com.soyle.stories.storyevent.time.adjust

import com.soyle.stories.domain.storyevent.StoryEvent

interface AdjustStoryEventsTimePrompt {
    fun promptForAdjustmentAmount(storyEventIds: Set<StoryEvent.Id>)
    fun promptForAdjustmentAmount(storyEventIds: Set<StoryEvent.Id>, amount: Long)
}