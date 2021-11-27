package com.soyle.stories.domain.scene.events

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.storyevent.StoryEvent

data class StoryEventAddedToScene(
    override val sceneId: Scene.Id,
    val storyEventId: StoryEvent.Id
) : SceneEvent()