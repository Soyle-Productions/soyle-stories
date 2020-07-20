package com.soyle.stories.theme.addValueWebToTheme

import com.soyle.stories.common.Notifier
import com.soyle.stories.theme.addSymbolicItemToOpposition.AddSymbolicItemToOppositionNotifier
import com.soyle.stories.theme.includeCharacterInTheme.IncludeCharacterInComparisonNotifier
import com.soyle.stories.theme.usecases.addSymbolicItemToOpposition.AddSymbolicItemToOpposition
import com.soyle.stories.theme.usecases.addSymbolicItemToOpposition.SymbolicRepresentationAddedToOpposition
import com.soyle.stories.theme.usecases.addValueWebToTheme.AddValueWebToTheme
import com.soyle.stories.theme.usecases.addValueWebToTheme.ValueWebAddedToTheme
import com.soyle.stories.theme.usecases.includeCharacterInComparison.IncludeCharacterInComparison
import kotlin.coroutines.coroutineContext

class AddValueWebToThemeNotifier(
    private val symbolicItemAddedToOppositionOutputPort: AddSymbolicItemToOpposition.OutputPort
) : Notifier<AddValueWebToTheme.OutputPort>(), AddValueWebToTheme.OutputPort {

    override suspend fun addedValueWebToTheme(response: AddValueWebToTheme.ResponseModel) {
        notifyAll(coroutineContext) { it.addedValueWebToTheme(response) }
        response.symbolicItemAdded?.let {
            symbolicItemAddedToOppositionOutputPort.addedSymbolicItemToOpposition(
                AddSymbolicItemToOpposition.ResponseModel(
                    it, listOf(), listOfNotNull(response.includedCharacter)
                )
            )
        }
    }

}