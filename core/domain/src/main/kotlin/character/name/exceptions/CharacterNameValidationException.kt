package com.soyle.stories.domain.character.name.exceptions

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.validation.ValidationException

data class CharacterNameValidationException(
    override val characterId: Character.Id,
    override val name: String,
    override val message: String?
) :
    ValidationException(), CharacterNameException

internal fun CannotRemoveDisplayName(characterId: Character.Id, name: String) =
    CharacterNameValidationException(characterId, name, "Cannot remove display name \"$name\"")