package com.soyle.stories.theme.addSymbolDialog

import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.ListAllCharacterArcs
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.location.usecases.listAllLocations.ListAllLocations
import com.soyle.stories.theme.addSymbolToTheme.AddSymbolToThemeController
import com.soyle.stories.theme.addSymbolicItemToOpposition.AddSymbolicItemToOppositionController
import com.soyle.stories.theme.usecases.addSymbolicItemToOpposition.AddSymbolicItemToOpposition
import com.soyle.stories.theme.usecases.listSymbolsInTheme.ListSymbolsInTheme
import java.util.*

class AddSymbolDialogController(
    themeId: String,
    private val oppositionId: String,
    private val threadTransformer: ThreadTransformer,
    private val listAllCharacterArcs: ListAllCharacterArcs,
    private val listAllLocations: ListAllLocations,
    private val listSymbolsInTheme: ListSymbolsInTheme,
    private val presenter: AddSymbolDialogPresenter,
    private val addSymbolicItemToOppositionController: AddSymbolicItemToOppositionController
) : AddSymbolDialogViewListener {

    private val themeId = UUID.fromString(themeId)

    override fun getValidState() {
        threadTransformer.async {
            listAllCharacterArcs.invoke(presenter)
            listAllLocations.invoke(presenter)
            listSymbolsInTheme.invoke(themeId, presenter)
        }
    }

    override fun selectCharacter(characterId: String) {
        addSymbolicItemToOppositionController.addCharacterToOpposition(oppositionId, characterId)
    }

    override fun selectLocation(locationId: String) {
        addSymbolicItemToOppositionController.addLocationToOpposition(oppositionId, locationId)
    }

    override fun selectSymbol(symbolId: String) {
        addSymbolicItemToOppositionController.addSymbolToOpposition(oppositionId, symbolId)
    }

}