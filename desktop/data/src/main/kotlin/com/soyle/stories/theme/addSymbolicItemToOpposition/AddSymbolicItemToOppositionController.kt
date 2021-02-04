package com.soyle.stories.theme.addSymbolicItemToOpposition

interface AddSymbolicItemToOppositionController {
    fun addCharacterToOpposition(oppositionId: String, characterId: String)
    fun addLocationToOpposition(oppositionId: String, locationId: String)
    fun addSymbolToOpposition(oppositionId: String, symbolId: String)
}