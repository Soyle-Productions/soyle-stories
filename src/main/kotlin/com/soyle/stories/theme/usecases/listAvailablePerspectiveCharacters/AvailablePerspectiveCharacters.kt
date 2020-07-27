package com.soyle.stories.theme.usecases.listAvailablePerspectiveCharacters

import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.CharacterItem
import java.util.*

class AvailablePerspectiveCharacters(
    val themeId: UUID,
    private val characters: List<CharacterItem>
) : List<CharacterItem> by characters