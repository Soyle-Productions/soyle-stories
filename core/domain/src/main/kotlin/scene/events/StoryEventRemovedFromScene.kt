package com.soyle.stories.domain.scene.events

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.character.events.CharacterInSceneEvent
import com.soyle.stories.domain.storyevent.StoryEvent

data class StoryEventRemovedFromScene(
    override val sceneId: Scene.Id,
    val storyEventId: StoryEvent.Id,
    val updatedCharacters: List<CharacterInSceneEvent>
) : SceneEvent()