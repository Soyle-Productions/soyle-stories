package com.soyle.stories.domain.character.events

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.validation.NonBlankString

class CharacterNameVariantRemoved(characterId: Character.Id, val variant: NonBlankString) : CharacterEvent(characterId) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CharacterNameVariantRemoved

        if (characterId != other.characterId) return false
        if (variant != other.variant) return false

        return true
    }

    override fun hashCode(): Int {
        return listOf(
            variant
        ).fold(characterId.hashCode()) { result, nextProp ->
            31 * result + nextProp.hashCode()
        }
    }

    override fun toString(): String {
        return "CharacterNameVariantRemoved(characterId=$characterId, variant=$variant)"
    }

}