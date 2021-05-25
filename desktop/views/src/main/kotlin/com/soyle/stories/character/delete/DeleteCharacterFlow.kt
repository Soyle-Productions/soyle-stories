package com.soyle.stories.character.delete

import com.soyle.stories.domain.character.Character

interface DeleteCharacterFlow {
    fun start(characterId: Character.Id, characterName: String)
}