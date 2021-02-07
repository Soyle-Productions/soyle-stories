package com.soyle.stories.usecase.theme.includeCharacterInComparison

import java.util.*

data class CharacterIncludedInTheme(
    val themeId: UUID,
    val themeName: String,
    val characterId: UUID,
    val characterName: String,
    val isMajorCharacter: Boolean
)

@Deprecated(message = "why is this even here?", level = DeprecationLevel.ERROR, replaceWith = ReplaceWith("CharacterIncludedInTheme(themeId, \"\", characterId, \"\", false)"))
class CharacterIncludedInThemeEvent(
    val themeId: UUID,
    val characterId: UUID,
    val includedCharacters: List<com.soyle.stories.usecase.character.listAllCharacterArcs.CharacterItem>
)