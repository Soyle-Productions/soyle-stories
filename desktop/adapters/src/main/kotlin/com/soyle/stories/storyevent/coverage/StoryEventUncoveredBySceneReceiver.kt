package com.soyle.stories.storyevent.coverage

import com.soyle.stories.domain.storyevent.events.StoryEventUncoveredFromScene

interface StoryEventUncoveredBySceneReceiver {
    suspend fun receiveStoryEventUncoveredByScene(event: StoryEventUncoveredFromScene)
}