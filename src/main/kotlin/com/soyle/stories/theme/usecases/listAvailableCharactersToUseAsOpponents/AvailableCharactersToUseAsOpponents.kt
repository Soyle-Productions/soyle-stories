package com.soyle.stories.theme.usecases.listAvailableCharactersToUseAsOpponents

import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.CharacterItem

class AvailableCharactersToUseAsOpponents(availableCharacters: List<CharacterItem>) : List<CharacterItem> by availableCharacters