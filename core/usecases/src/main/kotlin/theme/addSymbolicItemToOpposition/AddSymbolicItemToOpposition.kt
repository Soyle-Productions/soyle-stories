package com.soyle.stories.usecase.theme.addSymbolicItemToOpposition

import com.soyle.stories.domain.theme.oppositionValue.SymbolicRepresentationAddedToOpposition
import com.soyle.stories.usecase.theme.includeCharacterInComparison.CharacterIncludedInTheme
import com.soyle.stories.usecase.theme.removeSymbolicItem.RemovedSymbolicItem
import java.util.*

interface AddSymbolicItemToOpposition {

    suspend operator fun invoke(oppositionId: UUID, symbolicItemId: SymbolicItemId, output: OutputPort)

    @Deprecated(message = "Old api", replaceWith = ReplaceWith("this.invoke(oppositionId, CharacterId(characterId), output)", "com.soyle.stories.theme.usecases.addSymbolicItemToOpposition.CharacterId"))
    suspend fun addCharacterAsSymbol(oppositionId: UUID, characterId: UUID, output: OutputPort)
    @Deprecated(message = "Old api", replaceWith = ReplaceWith("this.invoke(oppositionId, LocationId(locationId), output)", "com.soyle.stories.theme.usecases.addSymbolicItemToOpposition.LocationId"))
    suspend fun addLocationAsSymbol(oppositionId: UUID, locationId: UUID, output: OutputPort)
    @Deprecated(message = "Old api", replaceWith = ReplaceWith("this.invoke(oppositionId, SymbolId(symbolId), output)", "com.soyle.stories.theme.usecases.addSymbolicItemToOpposition.SymbolId"))
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