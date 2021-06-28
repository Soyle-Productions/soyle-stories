package com.soyle.stories.theme.addValueWebToTheme

import com.soyle.stories.common.Notifier
import com.soyle.stories.usecase.theme.addSymbolicItemToOpposition.AddSymbolicItemToOpposition
import com.soyle.stories.usecase.theme.addValueWebToTheme.AddValueWebToTheme

class AddValueWebToThemeNotifier(
    private val symbolicItemAddedToOppositionOutputPort: AddSymbolicItemToOpposition.OutputPort
) : Notifier<AddValueWebToTheme.OutputPort>(), AddValueWebToTheme.OutputPort {

    override suspend fun addedValueWebToTheme(response: AddValueWebToTheme.ResponseModel) {
        notifyAll { it.addedValueWebToTheme(response) }
        response.symbolicItemAdded?.let {
            symbolicItemAddedToOppositionOutputPort.addedSymbolicItemToOpposition(
                AddSymbolicItemToOpposition.ResponseModel(
                    it, listOf(), listOfNotNull(response.includedCharacter)
                )
            )
        }
    }

}