package com.soyle.stories.storyevent.coverage

import com.soyle.stories.domain.storyevent.events.StoryEventCoveredByScene

interface StoryEventCoveredBySceneReceiver {
    suspend fun receiveStoryEventCoveredByScene(event: StoryEventCoveredByScene)
}