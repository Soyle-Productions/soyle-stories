package com.soyle.stories.theme.addOppositionToValueWeb

import com.soyle.stories.common.Notifier
import com.soyle.stories.usecase.theme.addOppositionToValueWeb.AddOppositionToValueWeb
import com.soyle.stories.usecase.theme.addSymbolicItemToOpposition.AddSymbolicItemToOpposition

class AddOppositionToValueWebNotifier(
    private val symbolicItemToOppositionOutputPort: AddSymbolicItemToOpposition.OutputPort
) : Notifier<AddOppositionToValueWeb.OutputPort>(), AddOppositionToValueWeb.OutputPort {

    override suspend fun addedOppositionToValueWeb(response: AddOppositionToValueWeb.ResponseModel) {
        notifyAll { it.addedOppositionToValueWeb(response) }
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