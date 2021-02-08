package com.soyle.stories.theme.addSymbolDialog

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.theme.addSymbolicItemToOpposition.AddSymbolicItemToOppositionController
import com.soyle.stories.usecase.theme.listAvailableEntitiesToAddToOpposition.ListAvailableEntitiesToAddToOpposition
import java.util.*

class AddSymbolDialogController(
    oppositionId: String,
    private val threadTransformer: ThreadTransformer,
    private val listAvailableEntitiesToAddToOpposition: ListAvailableEntitiesToAddToOpposition,
    private val presenter: AddSymbolDialogPresenter,
    private val addSymbolicItemToOppositionController: AddSymbolicItemToOppositionController
) : AddSymbolDialogViewListener {

    private val oppositionId = UUID.fromString(oppositionId)

    override fun getValidState() {
        threadTransformer.async {
            listAvailableEntitiesToAddToOpposition.invoke(oppositionId, presenter)
        }
    }

    override fun selectCharacter(characterId: String) {
        addSymbolicItemToOppositionController.addCharacterToOpposition(oppositionId.toString(), characterId)
    }

    override fun selectLocation(locationId: String) {
        addSymbolicItemToOppositionController.addLocationToOpposition(oppositionId.toString(), locationId)
    }

    override fun selectSymbol(symbolId: String) {
        addSymbolicItemToOppositionController.addSymbolToOpposition(oppositionId.toString(), symbolId)
    }

}