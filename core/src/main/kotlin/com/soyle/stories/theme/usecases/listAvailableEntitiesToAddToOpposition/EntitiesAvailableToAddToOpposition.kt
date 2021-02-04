package com.soyle.stories.theme.usecases.listAvailableEntitiesToAddToOpposition

import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.CharacterItem
import com.soyle.stories.location.usecases.listAllLocations.LocationItem
import com.soyle.stories.theme.usecases.SymbolItem

class EntitiesAvailableToAddToOpposition(
    val characters: List<CharacterItem>,
    val locations: List<LocationItem>,
    val symbols: List<SymbolItem>
)