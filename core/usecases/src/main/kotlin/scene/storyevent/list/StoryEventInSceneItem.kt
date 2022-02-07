package com.soyle.stories.usecase.scene.storyevent.list

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.usecase.character.arc.listAllCharacterArcs.CharacterItem

data class StoryEventInSceneItem(
    val storyEventId: StoryEvent.Id,
    val sceneId: Scene.Id,
    val storyEventName: String,
    val time: Long,
    val involvedCharacters: List<CharacterItem>
)