package com.soyle.stories.theme.removeSymbolicItem

import com.soyle.stories.character.removeCharacterFromStory.RemovedCharacterReceiver
import com.soyle.stories.character.usecases.removeCharacterFromStory.RemovedCharacter
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.location.deleteLocation.DeletedLocationReceiver
import com.soyle.stories.location.usecases.deleteLocation.DeletedLocation
import com.soyle.stories.theme.removeSymbolFromTheme.SymbolRemovedFromThemeReceiver
import com.soyle.stories.theme.usecases.removeSymbolFromTheme.SymbolRemovedFromTheme
import com.soyle.stories.theme.usecases.removeSymbolicItem.RemoveSymbolicItem
import java.util.*

class RemoveSymbolicItemControllerImpl(
    private val threadTransformer: ThreadTransformer,
    private val removeSymbolicItem: RemoveSymbolicItem,
    private val removeSymbolicItemOutputPort: RemoveSymbolicItem.OutputPort
) : RemoveSymbolicItemController, RemovedCharacterReceiver,
    DeletedLocationReceiver, SymbolRemovedFromThemeReceiver {

    override fun removeItemFromOpposition(oppositionId: String, itemId: String, onError: (Throwable) -> Unit) {
        val preparedOppositionId = UUID.fromString(oppositionId)
        val preparedItemId = UUID.fromString(itemId)
        threadTransformer.async {
            try {
                removeSymbolicItem.removeSymbolicItemFromOpposition(
                    preparedOppositionId,
                    preparedItemId,
                    removeSymbolicItemOutputPort
                )
            } catch (t: Throwable) { onError(t) }
        }
    }

    override suspend fun receiveCharacterRemoved(characterRemoved: RemovedCharacter) {
        threadTransformer.async {
            removeSymbolicItem.removeSymbolicItemFromAllThemes(
                characterRemoved.characterId,
                removeSymbolicItemOutputPort
            )
        }
    }

    override suspend fun receiveDeletedLocation(deletedLocation: DeletedLocation) {
        threadTransformer.async {
            removeSymbolicItem.removeSymbolicItemFromAllThemes(
                deletedLocation.location.uuid,
                removeSymbolicItemOutputPort
            )
        }
    }

    override suspend fun receiveSymbolRemovedFromTheme(symbolRemovedFromTheme: SymbolRemovedFromTheme) {
        removeSymbolicItem.removeSymbolicItemFromAllThemes(
            symbolRemovedFromTheme.symbolId,
            removeSymbolicItemOutputPort
        )
    }

}