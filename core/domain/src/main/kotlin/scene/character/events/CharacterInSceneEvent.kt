package com.soyle.stories.domain.scene.character.events

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.events.SceneEvent

abstract class CharacterInSceneEvent : SceneEvent() {
    abstract val characterId: Character.Id
}