package com.soyle.stories.domain.character.exceptions

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.validation.DuplicateOperationException

data class DuplicateCharacterOperationException(
    override val characterId: Character.Id,
    override val message: String
) : DuplicateOperationException(), CharacterException

internal fun characterAlreadyHasName(characterId: Character.Id, name: String) =
    DuplicateCharacterOperationException(characterId, "$characterId already has name \"$name\"")