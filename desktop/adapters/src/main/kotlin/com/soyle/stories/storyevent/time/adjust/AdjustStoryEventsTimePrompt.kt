package com.soyle.stories.storyevent.time.adjust

import com.soyle.stories.domain.storyevent.StoryEvent

interface AdjustStoryEventsTimePrompt : AutoCloseable {
    suspend fun requestAdjustmentAmount(): Long?
    suspend fun confirmAdjustmentAmount(amount: Long): Long?
}