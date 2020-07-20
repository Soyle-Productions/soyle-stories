package com.soyle.stories.theme.addOppositionToValueWeb

import com.soyle.stories.common.Notifier
import com.soyle.stories.common.Signaler
import com.soyle.stories.theme.addSymbolicItemToOpposition.AddSymbolicItemToOppositionNotifier
import com.soyle.stories.theme.includeCharacterInTheme.IncludeCharacterInComparisonNotifier
import com.soyle.stories.theme.usecases.addOppositionToValueWeb.AddOppositionToValueWeb
import com.soyle.stories.theme.usecases.addOppositionToValueWeb.OppositionAddedToValueWeb
import com.soyle.stories.theme.usecases.addSymbolicItemToOpposition.AddSymbolicItemToOpposition
import com.soyle.stories.theme.usecases.includeCharacterInComparison.IncludeCharacterInComparison
import kotlin.coroutines.coroutineContext

class AddOppositionToValueWebNotifier(
    private val symbolicItemToOppositionOutputPort: AddSymbolicItemToOpposition.OutputPort
) : Notifier<AddOppositionToValueWeb.OutputPort>(), AddOppositionToValueWeb.OutputPort {

    override suspend fun addedOppositionToValueWeb(response: AddOppositionToValueWeb.ResponseModel) {
        notifyAll(coroutineContext) { it.addedOppositionToValueWeb(response) }
        response.symbolicRepresentationAddedToOpposition?.let {
            symbolicItemToOppositionOutputPort.addedSymbolicItemToOpposition(
                AddSymbolicItemToOpposition.ResponseModel(
                    it,
                    listOfNotNull(response.symbolicRepresentationRemoved),
                    listOfNotNull(response.characterIncludedInTheme)
                )
            )
        }
    }
}