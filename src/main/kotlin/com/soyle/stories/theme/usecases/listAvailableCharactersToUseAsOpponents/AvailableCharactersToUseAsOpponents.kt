package com.soyle.stories.theme.usecases.listAvailableCharactersToUseAsOpponents

import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.CharacterItem
import java.util.*

class AvailableCharactersToUseAsOpponents(
    val themeId: UUID,
    val perspectiveCharacterId: UUID,
    availableCharacters: List<CharacterItem>) : List<CharacterItem> by availableCharacters