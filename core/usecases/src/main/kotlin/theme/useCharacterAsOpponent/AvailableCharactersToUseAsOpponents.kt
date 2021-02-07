package com.soyle.stories.usecase.theme.useCharacterAsOpponent

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