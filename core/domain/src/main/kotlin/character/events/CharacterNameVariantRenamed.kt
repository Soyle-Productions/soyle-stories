package com.soyle.stories.domain.character.events

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.validation.NonBlankString

class CharacterNameVariantRenamed(
    characterId: Character.Id,
    val originalVariant: NonBlankString,
    val newVariant: NonBlankString
) : CharacterEvent(characterId)
{

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CharacterNameVariantRenamed

        if (characterId != other.characterId) return false
        if (originalVariant != other.originalVariant) return false
        if (newVariant != other.newVariant) return false

        return true
    }

    override fun hashCode(): Int {
        return listOf(
            originalVariant, newVariant
        ).fold(characterId.hashCode()) { result, nextProp ->
            31 * result + nextProp.hashCode()
        }
    }

    override fun toString(): String {
        return "CharacterNameVariantRenamed(characterId=$characterId, originalVariant=$originalVariant, newVariant=$newVariant)"
    }
}