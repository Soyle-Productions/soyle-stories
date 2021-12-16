package com.soyle.stories.domain.character.name.events

import com.soyle.stories.domain.character.events.CharacterEvent

abstract class CharacterNameChange : CharacterEvent() {
    abstract val name: String
}