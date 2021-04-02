package com.soyle.stories.usecase.theme.listAvailableEntitiesToAddToOpposition

import com.soyle.stories.usecase.character.arc.listAllCharacterArcs.CharacterItem
import com.soyle.stories.usecase.location.listAllLocations.LocationItem
import com.soyle.stories.usecase.theme.SymbolItem

class EntitiesAvailableToAddToOpposition(
    val characters: List<CharacterItem>,
    val locations: List<LocationItem>,
    val symbols: List<SymbolItem>
)