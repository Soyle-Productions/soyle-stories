package com.soyle.stories.theme.addSymbolicItemToOpposition

import com.soyle.stories.common.Notifier
import com.soyle.stories.theme.usecases.addSymbolicItemToOpposition.AddSymbolicItemToOpposition
import com.soyle.stories.theme.usecases.includeCharacterInComparison.IncludeCharacterInComparison
import com.soyle.stories.theme.usecases.removeSymbolicItem.RemoveSymbolicItem
import kotlin.coroutines.coroutineContext

class AddSymbolicItemToOppositionNotifier(
    private val removeSymbolicItemOutputPort: RemoveSymbolicItem.OutputPort,
    private val includeCharacterInComparisonOutputPort: IncludeCharacterInComparison.OutputPort
) : Notifier<AddSymbolicItemToOpposition.OutputPort>(), AddSymbolicItemToOpposition.OutputPort {

    override suspend fun addedSymbolicItemToOpposition(response: AddSymbolicItemToOpposition.ResponseModel) {
        if (response.removedSymbolicItems.isNotEmpty()) {
            removeSymbolicItemOutputPort.symbolicItemsRemoved(response.removedSymbolicItems)
        }
        if (response.includedCharacters.isNotEmpty()) {
            response.includedCharacters.forEach {
                includeCharacterInComparisonOutputPort.receiveIncludeCharacterInComparisonResponse(it)
            }
        }
        notifyAll { it.addedSymbolicItemToOpposition(response) }
    }
}
