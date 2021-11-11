package com.soyle.stories.domain.storyevent.events

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.storyevent.StoryEvent

data class StoryEventCoveredByScene(
    override val storyEventId: StoryEvent.Id,
    val sceneId: Scene.Id,
    val uncovered: StoryEventUncoveredFromScene?
) : StoryEventChange()