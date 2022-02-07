package com.soyle.stories.storyevent.coverage.cover

import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.usecase.storyevent.StoryEventItem

fun interface CoverStoryEventPrompt {
    suspend fun requestStoryEventToCover(items: List<StoryEventItem>): StoryEvent.Id?
}