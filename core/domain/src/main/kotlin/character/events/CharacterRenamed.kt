package com.soyle.stories.domain.character.events

import com.soyle.stories.domain.character.Character

class CharacterRenamed(characterId: Character.Id, val newName: String) : CharacterEvent(characterId) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CharacterRenamed

        if (newName != other.newName) return false
        if (characterId != other.characterId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = newName.hashCode()
        result = 31 * result + characterId.hashCode()
        return result
    }


}