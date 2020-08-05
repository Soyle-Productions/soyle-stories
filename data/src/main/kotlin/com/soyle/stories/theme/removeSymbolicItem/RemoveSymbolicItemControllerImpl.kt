package com.soyle.stories.theme.removeSymbolicItem

import com.soyle.stories.character.removeCharacterFromStory.RemovedCharacterReceiver
import com.soyle.stories.character.usecases.removeCharacterFromStory.RemoveCharacterFromStory
import com.soyle.stories.character.usecases.removeCharacterFromStory.RemovedCharacter
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.location.LocationException
import com.soyle.stories.location.usecases.deleteLocation.DeleteLocation
import com.soyle.stories.theme.usecases.removeSymbolFromTheme.RemoveSymbolFromTheme
import com.soyle.stories.theme.usecases.removeSymbolFromTheme.SymbolRemovedFromTheme
import com.soyle.stories.theme.usecases.removeSymbolicItem.RemoveSymbolicItem
import java.util.*

class RemoveSymbolicItemControllerImpl(
    private val threadTransformer: ThreadTransformer,
    private val removeSymbolicItem: RemoveSymbolicItem,
    private val removeSymbolicItemOutputPort: RemoveSymbolicItem.OutputPort
) : RemoveSymbolicItemController, RemovedCharacterReceiver,
    DeleteLocation.OutputPort, RemoveSymbolFromTheme.OutputPort {

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

    override fun receiveDeleteLocationResponse(response: DeleteLocation.ResponseModel) {
        threadTransformer.async {
            removeSymbolicItem.removeSymbolicItemFromAllThemes(
                response.locationId,
                removeSymbolicItemOutputPort
            )
        }
    }

    override suspend fun removedSymbolFromTheme(response: SymbolRemovedFromTheme) {
        removeSymbolicItem.removeSymbolicItemFromAllThemes(
            response.symbolId,
            removeSymbolicItemOutputPort
        )
    }

    override fun receiveDeleteLocationFailure(failure: LocationException) {}

}