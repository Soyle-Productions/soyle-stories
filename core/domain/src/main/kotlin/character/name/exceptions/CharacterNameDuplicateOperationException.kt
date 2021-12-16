package com.soyle.stories.domain.character.name.exceptions

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.validation.DuplicateOperationException

data class CharacterNameDuplicateOperationException(
    override val characterId: Character.Id,
    override val name: String,
    override val message: String?
) : DuplicateOperationException(), CharacterNameException

internal fun CharacterAlreadyHasName(characterId: Character.Id, name: String) =
    CharacterNameDuplicateOperationException(characterId, name, "$characterId already has name \"$name\"")

internal fun CharacterDisplayNameAlreadySet(characterId: Character.Id, name: String) =
    CharacterNameDuplicateOperationException(characterId, name, "$characterId already has display name \"$name\"")