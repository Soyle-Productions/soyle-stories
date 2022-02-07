package com.soyle.stories.usecase.storyevent.coverage.cover

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.storyevent.events.StoryEventCoveredByScene

interface CoverStoryEventInScene {
    suspend operator fun invoke(storyEventId: StoryEvent.Id, sceneId: Scene.Id, output: OutputPort): Result<Nothing?>

    fun interface OutputPort {
        suspend fun storyEventCoveredByScene(response: StoryEventCoveredByScene)
    }

}