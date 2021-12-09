package com.soyle.stories.domain.character.exceptions

import com.soyle.stories.domain.character.Character

interface CharacterException {
    val characterId: Character.Id
}