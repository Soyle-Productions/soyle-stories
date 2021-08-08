package com.soyle.stories.desktop.view.theme.characterComparison.doubles

import com.soyle.stories.theme.addSymbolicItemToOpposition.AddSymbolicItemToOppositionController

class AddSymbolicItemToOppositionControllerDouble(
    val onAddCharacterToOpposition: (String, String) -> Unit = { _, _ -> },
    val onAddLocationToOpposition: (String, String) -> Unit = { _, _ -> },
    val onAddSymbolToOpposition: (String, String) -> Unit = { _, _ -> }
) : AddSymbolicItemToOppositionController {

    override fun addCharacterToOpposition(oppositionId: String, characterId: String) {
        onAddCharacterToOpposition(oppositionId, characterId)
    }

    override fun addLocationToOpposition(oppositionId: String, locationId: String) {
        onAddLocationToOpposition(oppositionId, locationId)
    }

    override fun addSymbolToOpposition(oppositionId: String, symbolId: String) {
        onAddSymbolToOpposition(oppositionId, symbolId)
    }
}