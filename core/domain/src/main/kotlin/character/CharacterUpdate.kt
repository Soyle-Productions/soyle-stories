package com.soyle.stories.domain.character

import com.soyle.stories.domain.entities.EntityUpdate
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

sealed class CharacterUpdate<out Event>(val character: Character) : EntityUpdate<Character> {

    override fun component1(): Character = character

    class WithoutChange(character: Character, val reason: CharacterException? = null) : CharacterUpdate<Nothing>(character)
    class Updated<Event>(character: Character, val event: Event) : CharacterUpdate<Event>(character) {

        operator fun component2(): Event = event
    }
}