package com.soyle.stories.usecase.character.listCharactersAvailableToIncludeInTheme

import com.soyle.stories.usecase.character.listAllCharacterArcs.CharacterItem
import java.util.*

class CharactersAvailableToIncludeInTheme(
    val themeId: UUID,
    private val characters: List<CharacterItem>
) : List<CharacterItem> by characters