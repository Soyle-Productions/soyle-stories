package com.soyle.stories.theme.addSymbolicItemToOpposition

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.usecase.theme.addSymbolicItemToOpposition.AddSymbolicItemToOpposition
import com.soyle.stories.usecase.theme.addSymbolicItemToOpposition.CharacterId
import com.soyle.stories.usecase.theme.addSymbolicItemToOpposition.LocationId
import com.soyle.stories.usecase.theme.addSymbolicItemToOpposition.SymbolId
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
            addSymbolicItemToOpposition.invoke(
                preparedOppositionId,
                CharacterId(preparedCharacterId), addSymbolicItemToOppositionOutputPort
            )
        }
    }

    override fun addLocationToOpposition(oppositionId: String, locationId: String) {
        val preparedOppositionId = prepareOppositionId(oppositionId)
        val preparedLocationId = UUID.fromString(locationId)
        threadTransformer.async {
            addSymbolicItemToOpposition.invoke(
                preparedOppositionId,
                LocationId(preparedLocationId), addSymbolicItemToOppositionOutputPort
            )
        }
    }

    override fun addSymbolToOpposition(oppositionId: String, symbolId: String) {
        val preparedOppositionId = prepareOppositionId(oppositionId)
        val preparedSymbolId = UUID.fromString(symbolId)
        threadTransformer.async {
            addSymbolicItemToOpposition.invoke(
                preparedOppositionId,
                SymbolId(preparedSymbolId), addSymbolicItemToOppositionOutputPort
            )
        }
    }

    private fun prepareOppositionId(oppositionId: String) = UUID.fromString(oppositionId)

}