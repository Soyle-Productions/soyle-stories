package com.soyle.stories.theme.renameSymbolicItems

import com.soyle.stories.character.renameCharacter.CharacterRenamedReceiver
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.character.name.events.CharacterRenamed
import com.soyle.stories.domain.location.events.LocationRenamed
import com.soyle.stories.location.renameLocation.LocationRenamedReceiver
import com.soyle.stories.theme.renameSymbol.RenamedSymbolReceiver
import com.soyle.stories.usecase.theme.renameSymbol.RenamedSymbol
import com.soyle.stories.usecase.theme.renameSymbolicItems.RenameSymbolicItem

class RenameSymbolicItemController(
    private val threadTransformer: ThreadTransformer,
    private val renameSymbolicItem: RenameSymbolicItem,
    private val renameSymbolicItemOutputPort: RenameSymbolicItem.OutputPort
) : CharacterRenamedReceiver, LocationRenamedReceiver, RenamedSymbolReceiver {

    override suspend fun receiveCharacterRenamed(characterRenamed: CharacterRenamed) {
        threadTransformer.async {
            renameSymbolicItem.invoke(characterRenamed.characterId.uuid, characterRenamed.name, renameSymbolicItemOutputPort)
        }
    }

    override suspend fun receiveLocationRenamed(locationRenamed: LocationRenamed) {
        renameSymbolicItem.invoke(locationRenamed.locationId.uuid, locationRenamed.newName, renameSymbolicItemOutputPort)
    }

    override suspend fun receiveRenamedSymbol(renamedSymbol: RenamedSymbol) {
        renameSymbolicItem.invoke(renamedSymbol.symbolId, renamedSymbol.newName, renameSymbolicItemOutputPort)
    }

}