package com.soyle.stories.storyevent.coverage

import com.soyle.stories.domain.scene.events.StoryEventRemovedFromScene
import com.soyle.stories.domain.storyevent.events.StoryEventUncoveredFromScene
import com.soyle.stories.scene.outline.StoryEventRemovedFromSceneReceiver
import com.soyle.stories.usecase.storyevent.coverage.uncover.UncoverStoryEventFromScene
import java.util.logging.Logger

class UncoverStoryEventFromSceneOutput(
    private val storyEventUncoveredFromSceneReceiver: StoryEventUncoveredBySceneReceiver,
    private val storyEventRemovedFromSceneReceiver: StoryEventRemovedFromSceneReceiver
) : UncoverStoryEventFromScene.OutputPort {
    override suspend fun storyEventUncoveredFromScene(
        storyEventUncoveredFromScene: StoryEventUncoveredFromScene,
        storyEventRemovedFromScene: Result<StoryEventRemovedFromScene>
    ) {
        storyEventUncoveredFromSceneReceiver.receiveStoryEventUncoveredByScene(storyEventUncoveredFromScene)
        when {
            storyEventRemovedFromScene.isSuccess -> storyEventRemovedFromSceneReceiver.receiveStoryEventRemovedFromScene(
                storyEventRemovedFromScene.getOrThrow()
            )
            else -> storyEventRemovedFromScene.exceptionOrNull()?.let {
                Logger.getGlobal().warning(it.stackTrace.toString())
            }
        }
    }
}