package com.soyle.stories.domain.character.events

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.validation.NonBlankString

data class CharacterNameVariantRenamed(
    override val characterId: Character.Id,
    val originalVariant: NonBlankString,
    val newVariant: NonBlankString
) : CharacterEvent()