package com.soyle.stories.theme.usecases.listAvailableCharactersToUseAsOpponents

import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.CharacterItem
import java.util.*

class AvailableCharactersToUseAsOpponents(
    val themeId: UUID,
    val perspectiveCharacterId: UUID,
    availableCharacters: List<AvailableCharacterToUseAsOpponent>
) : List<AvailableCharacterToUseAsOpponent> by availableCharacters

class AvailableCharacterToUseAsOpponent(
    val characterId: UUID,
    val characterName: String,
    val mediaId: UUID?,
    val includedInTheme: Boolean
)