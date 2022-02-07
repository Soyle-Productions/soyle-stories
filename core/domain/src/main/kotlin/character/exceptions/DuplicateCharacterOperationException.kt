package com.soyle.stories.domain.character.exceptions

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.validation.DuplicateOperationException

data class DuplicateCharacterOperationException(
    override val characterId: Character.Id,
    override val message: String
) : DuplicateOperationException(), CharacterException

fun CharacterAlreadyRemovedFromStory(characterId: Character.Id) =
    DuplicateCharacterOperationException(characterId, "$characterId already removed from story")