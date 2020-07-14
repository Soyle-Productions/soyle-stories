package com.soyle.stories.theme.usecases.addSymbolicItemToOpposition

import com.soyle.stories.theme.usecases.includeCharacterInComparison.CharacterIncludedInTheme
import com.soyle.stories.theme.usecases.removeSymbolicItem.RemovedSymbolicItem
import java.util.*

interface AddSymbolicItemToOpposition {

    suspend fun addCharacterAsSymbol(oppositionId: UUID, characterId: UUID, output: OutputPort)
    suspend fun addLocationAsSymbol(oppositionId: UUID, locationId: UUID, output: OutputPort)
    suspend fun addSymbolAsSymbol(oppositionId: UUID, symbolId: UUID, output: OutputPort)

    class ResponseModel(
        val addedSymbolicItem: SymbolicRepresentationAddedToOpposition,
        val removedSymbolicItems: List<RemovedSymbolicItem>,
        val includedCharacters: List<CharacterIncludedInTheme>
    )

    interface OutputPort {
        suspend fun addedSymbolicItemToOpposition(response: ResponseModel)
    }

}