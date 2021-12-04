package com.soyle.stories.storyevent.coverage

import com.soyle.stories.scene.outline.StoryEventAddedToSceneReceiver
import com.soyle.stories.scene.outline.StoryEventRemovedFromSceneReceiver
import com.soyle.stories.usecase.storyevent.coverage.cover.CoverStoryEventInScene

class CoverStoryEventInSceneOutput(
    private val storyEventRemovedFromSceneReceiver: StoryEventRemovedFromSceneReceiver,
    private val storyEventAddedToSceneReceiver: StoryEventAddedToSceneReceiver,
    private val storyEventCoveredBySceneReceiver: StoryEventCoveredBySceneReceiver,
    private val storyEventUncoveredBySceneReceiver: StoryEventUncoveredBySceneReceiver,
) : CoverStoryEventInScene.OutputPort {
    override suspend fun receiveCoverStoryEventInSceneResponse(response: CoverStoryEventInScene.ResponseModel) {
        response.storyEventRemovedFromScene?.let { storyEventRemovedFromSceneReceiver.receiveStoryEventRemovedFromScene(it) }
        response.storyEventUncoveredFromScene?.let { storyEventUncoveredBySceneReceiver.receiveStoryEventUncoveredByScene(it) }
        response.storyEventCoveredByScene?.let { storyEventCoveredBySceneReceiver.receiveStoryEventCoveredByScene(it) }
        response.storyEventAddedToScene?.let { storyEventAddedToSceneReceiver.receiveStoryEventAddedToScene(it) }
        response.storyEventCoveredByScene?.uncovered?.let { storyEventUncoveredBySceneReceiver.receiveStoryEventUncoveredByScene(it) }
    }
}