package com.soyle.stories.theme.usecases.listAvailablePerspectiveCharacters

import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.CharacterItem
import java.util.*

class AvailablePerspectiveCharacters(
    val themeId: UUID,
    private val characters: List<AvailablePerspectiveCharacter>
) : List<AvailablePerspectiveCharacter> by characters

class AvailablePerspectiveCharacter(
    val characterId: UUID,
    val characterName: String,
    val isMajorCharacter: Boolean
)