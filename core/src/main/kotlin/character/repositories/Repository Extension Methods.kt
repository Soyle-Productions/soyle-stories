package com.soyle.stories.character.repositories

import com.soyle.stories.character.CharacterDoesNotExist
import com.soyle.stories.entities.Character
import java.util.*

suspend fun CharacterRepository.getCharacterOrError(characterId: UUID) = getCharacterById(Character.Id(characterId))
    ?: throw CharacterDoesNotExist(characterId)