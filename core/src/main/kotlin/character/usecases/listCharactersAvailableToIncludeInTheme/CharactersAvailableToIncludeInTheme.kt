package com.soyle.stories.character.usecases.listCharactersAvailableToIncludeInTheme

import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.CharacterItem
import java.util.*

class CharactersAvailableToIncludeInTheme(
    val themeId: UUID,
    private val characters: List<CharacterItem>
) : List<CharacterItem> by characters