package com.soyle.stories.storyevent.coverage.uncover

import com.soyle.stories.domain.storyevent.events.StoryEventUncoveredFromScene

fun interface StoryEventUncoveredBySceneReceiver {
    suspend fun receiveStoryEventUncoveredByScene(event: StoryEventUncoveredFromScene)
}