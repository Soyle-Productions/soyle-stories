package com.soyle.stories.domain.character

import com.soyle.stories.domain.entities.updates.Change
import com.soyle.stories.domain.entities.updates.Update

sealed class CharacterUpdate<out Event>(val character: Character) : Update<Character> {

    override fun component1(): Character = character

    class WithoutChange(character: Character, val reason: CharacterException? = null) : CharacterUpdate<Nothing>(character)
    class Updated<out Event>(character: Character, val event: Event) : CharacterUpdate<Event>(character) {

        operator fun component2(): Event = event
    }
}