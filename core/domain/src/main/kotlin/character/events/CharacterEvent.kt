package com.soyle.stories.domain.character.events

import com.soyle.stories.domain.character.Character

abstract class CharacterEvent {
    abstract val characterId: Character.Id
}