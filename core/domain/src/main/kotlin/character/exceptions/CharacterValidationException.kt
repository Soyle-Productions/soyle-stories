package com.soyle.stories.domain.character.exceptions

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.validation.ValidationException

data class CharacterValidationException(
    override val characterId: Character.Id,
    override val message: String?
) : ValidationException(), CharacterException
