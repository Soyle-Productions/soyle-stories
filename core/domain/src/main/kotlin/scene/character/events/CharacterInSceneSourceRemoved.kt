package com.soyle.stories.domain.scene.character.events

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.storyevent.StoryEvent

data class CharacterInSceneSourceRemoved(
    override val sceneId: Scene.Id,
    override val characterId: Character.Id,
    val storyEventId: StoryEvent.Id
    ) : CharacterInSceneEvent()