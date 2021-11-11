package com.soyle.stories.domain.storyevent.events

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.storyevent.StoryEvent

data class StoryEventUncoveredFromScene(
    override val storyEventId: StoryEvent.Id,
    val previousSceneId: Scene.Id
) : StoryEventChange()