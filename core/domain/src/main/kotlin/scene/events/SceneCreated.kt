package com.soyle.stories.domain.scene.events

import com.soyle.stories.domain.prose.Prose
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.storyevent.StoryEvent

data class SceneCreated(
    override val sceneId: Scene.Id,
    val name: String,
    val proseId: Prose.Id,
    val storyEventId: StoryEvent.Id
) : SceneEvent()