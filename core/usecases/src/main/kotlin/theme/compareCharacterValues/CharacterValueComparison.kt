package com.soyle.stories.usecase.theme.compareCharacterValues

import java.util.*

class CharacterValueComparison(
    val themeId: UUID,
    val characters: List<CharacterComparedWithValues>
)

class CharacterComparedWithValues(
    val characterId: UUID,
    val characterName: String,
    val characterArchetype: String,
    val characterValues: List<CharacterValue>,
    val isMajorCharacter: Boolean
)

class CharacterValue(
    val valueWebId: UUID,
    val valueWebName: String,
    val oppositionId: UUID,
    val oppositionName: String
)