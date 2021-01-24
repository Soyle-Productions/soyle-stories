package com.soyle.stories.theme.renameSymbolicItems

import com.soyle.stories.character.renameCharacter.CharacterRenamedReceiver
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.entities.CharacterRenamed
import com.soyle.stories.entities.LocationRenamed
import com.soyle.stories.location.renameLocation.LocationRenamedReceiver
import com.soyle.stories.theme.usecases.renameSymbol.RenameSymbol
import com.soyle.stories.theme.usecases.renameSymbol.RenamedSymbol
import com.soyle.stories.theme.usecases.renameSymbolicItems.RenameSymbolicItem

class RenameSymbolicItemController(
    private val threadTransformer: ThreadTransformer,
    private val renameSymbolicItem: RenameSymbolicItem,
    private val renameSymbolicItemOutputPort: RenameSymbolicItem.OutputPort
) : CharacterRenamedReceiver, LocationRenamedReceiver, RenameSymbol.OutputPort {

    override suspend fun receiveCharacterRenamed(characterRenamed: CharacterRenamed) {
        threadTransformer.async {
            renameSymbolicItem.invoke(characterRenamed.characterId.uuid, characterRenamed.newName, renameSymbolicItemOutputPort)
        }
    }

    override suspend fun receiveLocationRenamed(locationRenamed: LocationRenamed) {
        renameSymbolicItem.invoke(locationRenamed.locationId.uuid, locationRenamed.newName, renameSymbolicItemOutputPort)
    }

    override suspend fun symbolRenamed(response: RenamedSymbol) {
        renameSymbolicItem.invoke(response.symbolId, response.newName, renameSymbolicItemOutputPort)
    }

}