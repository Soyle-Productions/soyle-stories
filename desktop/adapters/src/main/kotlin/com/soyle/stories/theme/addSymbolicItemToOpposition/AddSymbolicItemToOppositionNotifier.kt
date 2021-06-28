package com.soyle.stories.theme.addSymbolicItemToOpposition

import com.soyle.stories.common.Notifier
import com.soyle.stories.usecase.theme.addSymbolicItemToOpposition.AddSymbolicItemToOpposition
import com.soyle.stories.usecase.theme.includeCharacterInComparison.IncludeCharacterInComparison
import com.soyle.stories.usecase.theme.removeSymbolicItem.RemoveSymbolicItem

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
