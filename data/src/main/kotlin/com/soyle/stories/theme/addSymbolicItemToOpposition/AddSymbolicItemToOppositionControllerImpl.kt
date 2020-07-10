package com.soyle.stories.theme.addSymbolicItemToOpposition

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.theme.usecases.addSymbolicItemToOpposition.AddSymbolicItemToOpposition
import java.util.*

class AddSymbolicItemToOppositionControllerImpl(
    private val threadTransformer: ThreadTransformer,
    private val addSymbolicItemToOpposition: AddSymbolicItemToOpposition,
    private val addSymbolicItemToOppositionOutputPort: AddSymbolicItemToOpposition.OutputPort
) : AddSymbolicItemToOppositionController {

    override fun addCharacterToOpposition(oppositionId: String, characterId: String) {
        val preparedOppositionId = prepareOppositionId(oppositionId)
        val preparedCharacterId = UUID.fromString(characterId)
        threadTransformer.async {
            addSymbolicItemToOpposition.addCharacterAsSymbol(
                preparedOppositionId,
                preparedCharacterId,
                addSymbolicItemToOppositionOutputPort
            )
        }
    }

    override fun addLocationToOpposition(oppositionId: String, locationId: String) {
        val preparedOppositionId = prepareOppositionId(oppositionId)
        val preparedLocationId = UUID.fromString(locationId)
        threadTransformer.async {
            addSymbolicItemToOpposition.addLocationAsSymbol(
                preparedOppositionId,
                preparedLocationId,
                addSymbolicItemToOppositionOutputPort
            )
        }
    }

    override fun addSymbolToOpposition(oppositionId: String, symbolId: String) {
        val preparedOppositionId = prepareOppositionId(oppositionId)
        val preparedSymbolId = UUID.fromString(symbolId)
        threadTransformer.async {
            addSymbolicItemToOpposition.addSymbolAsSymbol(
                preparedOppositionId,
                preparedSymbolId,
                addSymbolicItemToOppositionOutputPort
            )
        }
    }

    private fun prepareOppositionId(oppositionId: String) = UUID.fromString(oppositionId)

}