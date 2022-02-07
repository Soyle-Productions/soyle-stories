package com.soyle.stories.storyevent.coverage.cover

import com.soyle.stories.domain.storyevent.events.StoryEventCoveredByScene
import com.soyle.stories.storyevent.coverage.StoryEventCoveredBySceneReceiver
import com.soyle.stories.usecase.storyevent.coverage.cover.CoverStoryEventInScene

class CoverStoryEventInSceneOutput(
    private val storyEventCoveredBySceneReceiver: StoryEventCoveredBySceneReceiver,
) : CoverStoryEventInScene.OutputPort {

    override suspend fun storyEventCoveredByScene(response: StoryEventCoveredByScene) {
        storyEventCoveredBySceneReceiver.receiveStoryEventCoveredByScene(response)
    }

}