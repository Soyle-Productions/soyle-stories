package com.soyle.stories.storyevent.coverage.uncover

import com.soyle.stories.domain.storyevent.events.StoryEventUncoveredFromScene
import com.soyle.stories.usecase.storyevent.coverage.uncover.UncoverStoryEventFromScene

class UncoverStoryEventFromSceneOutput(
    private val storyEventUncoveredFromSceneReceiver: StoryEventUncoveredBySceneReceiver
) : UncoverStoryEventFromScene.OutputPort {

    override suspend fun storyEventUncoveredFromScene(storyEventUncoveredFromScene: StoryEventUncoveredFromScene) {
        storyEventUncoveredFromSceneReceiver.receiveStoryEventUncoveredByScene(storyEventUncoveredFromScene)
    }
}