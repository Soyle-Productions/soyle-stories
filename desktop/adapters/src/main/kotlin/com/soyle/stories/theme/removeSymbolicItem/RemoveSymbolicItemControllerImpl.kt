package com.soyle.stories.theme.removeSymbolicItem

import com.soyle.stories.common.Receiver
import com.soyle.stories.usecase.character.remove.RemovedCharacter
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.character.events.CharacterRemovedFromStory
import com.soyle.stories.location.deleteLocation.DeletedLocationReceiver
import com.soyle.stories.usecase.location.deleteLocation.DeletedLocation
import com.soyle.stories.theme.removeSymbolFromTheme.SymbolRemovedFromThemeReceiver
import com.soyle.stories.usecase.theme.removeSymbolFromTheme.SymbolRemovedFromTheme
import com.soyle.stories.usecase.theme.removeSymbolicItem.RemoveSymbolicItem
import java.util.*

class RemoveSymbolicItemControllerImpl(
    private val threadTransformer: ThreadTransformer,
    private val removeSymbolicItem: RemoveSymbolicItem,
    private val removeSymbolicItemOutputPort: RemoveSymbolicItem.OutputPort
) : RemoveSymbolicItemController, Receiver<CharacterRemovedFromStory>,
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

    override suspend fun receiveEvent(event: CharacterRemovedFromStory) {
        threadTransformer.async {
            removeSymbolicItem.removeSymbolicItemFromAllThemes(
                event.characterId.uuid,
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