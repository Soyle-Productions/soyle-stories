package com.soyle.stories.theme.addSymbolicItemToOpposition

import com.soyle.stories.common.Notifier
import com.soyle.stories.theme.usecases.addSymbolicItemToOpposition.AddSymbolicItemToOpposition
import com.soyle.stories.theme.usecases.addSymbolicItemToOpposition.SymbolicRepresentationAddedToOpposition
import kotlin.coroutines.coroutineContext

class AddSymbolicItemToOppositionNotifier : Notifier<AddSymbolicItemToOpposition.OutputPort>(), AddSymbolicItemToOpposition.OutputPort {

    override suspend fun addedSymbolicItemToOpposition(response: SymbolicRepresentationAddedToOpposition) {
        notifyAll(coroutineContext) { it.addedSymbolicItemToOpposition(response) }
    }
}