package com.soyle.stories.storyevent.coverage.uncover

import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.usecase.scene.storyevent.list.StoryEventInSceneItem
import com.soyle.stories.usecase.storyevent.StoryEventItem

interface UncoverStoryEventPrompt {
    suspend fun displayFailureToListStoryEvents(failure: Throwable)
    suspend fun requestStoryEventToUncover(storyEventsInScene: List<StoryEventInSceneItem>): StoryEvent.Id
}