package com.soyle.stories.theme.usecases.includeCharacterInComparison

import java.util.*

class CharacterIncludedInTheme(
    val themeId: UUID,
    val characterId: UUID,
    val includedCharacters: List<com.soyle.stories.characterarc.usecases.listAllCharacterArcs.CharacterItem>
)