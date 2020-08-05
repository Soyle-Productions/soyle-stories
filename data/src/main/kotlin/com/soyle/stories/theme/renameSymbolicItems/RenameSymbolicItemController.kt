package com.soyle.stories.theme.renameSymbolicItems

import com.soyle.stories.character.CharacterException
import com.soyle.stories.character.renameCharacter.RenamedCharacterReceiver
import com.soyle.stories.character.usecases.renameCharacter.RenameCharacter
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.location.LocationException
import com.soyle.stories.location.usecases.renameLocation.RenameLocation
import com.soyle.stories.theme.usecases.renameSymbol.RenameSymbol
import com.soyle.stories.theme.usecases.renameSymbol.RenamedSymbol
import com.soyle.stories.theme.usecases.renameSymbolicItems.RenameSymbolicItem

class RenameSymbolicItemController(
    private val threadTransformer: ThreadTransformer,
    private val renameSymbolicItem: RenameSymbolicItem,
    private val renameSymbolicItemOutputPort: RenameSymbolicItem.OutputPort
) : RenamedCharacterReceiver, RenameLocation.OutputPort, RenameSymbol.OutputPort {

    override suspend fun receiveRenamedCharacter(renamedCharacter: RenameCharacter.ResponseModel) {
        threadTransformer.async {
            renameSymbolicItem.invoke(renamedCharacter.characterId, renamedCharacter.newName, renameSymbolicItemOutputPort)
        }
    }

    override fun receiveRenameLocationResponse(response: RenameLocation.ResponseModel) {
        threadTransformer.async {
            renameSymbolicItem.invoke(response.locationId, response.newName, renameSymbolicItemOutputPort)
        }
    }

    override suspend fun symbolRenamed(response: RenamedSymbol) {
        renameSymbolicItem.invoke(response.symbolId, response.newName, renameSymbolicItemOutputPort)
    }

    override fun receiveRenameLocationFailure(failure: LocationException) {}

}