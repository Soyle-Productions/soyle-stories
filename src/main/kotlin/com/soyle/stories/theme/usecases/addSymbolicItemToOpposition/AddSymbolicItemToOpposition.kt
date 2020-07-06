package com.soyle.stories.theme.usecases.addSymbolicItemToOpposition

import java.util.*

interface AddSymbolicItemToOpposition {

    suspend fun addCharacterAsSymbol(oppositionId: UUID, characterId: UUID, output: OutputPort)
    suspend fun addLocationAsSymbol(oppositionId: UUID, locationId: UUID, output: OutputPort)
    suspend fun addSymbolAsSymbol(oppositionId: UUID, symbolId: UUID, output: OutputPort)

    interface OutputPort {
        suspend fun addedSymbolicItemToOpposition(response: SymbolicRepresentationAddedToOpposition)
    }

}