package com.soyle.stories.usecase.storyevent.coverage.cover

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.events.StoryEventAddedToScene
import com.soyle.stories.domain.scene.events.StoryEventRemovedFromScene
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.storyevent.events.StoryEventCoveredByScene
import com.soyle.stories.domain.storyevent.events.StoryEventUncoveredFromScene

interface CoverStoryEventInScene {
    suspend operator fun invoke(storyEventId: StoryEvent.Id, sceneId: Scene.Id, output: OutputPort)

    class ResponseModel(
        val storyEventAddedToScene: StoryEventAddedToScene?,
        val storyEventCoveredByScene: StoryEventCoveredByScene?,
        val storyEventRemovedFromScene: StoryEventRemovedFromScene?,
        val storyEventUncoveredFromScene: StoryEventUncoveredFromScene?
    )

    fun interface OutputPort {
        suspend fun receiveCoverStoryEventInSceneResponse(response: ResponseModel)
    }

}