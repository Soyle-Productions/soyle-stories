package com.soyle.stories.theme.addOppositionToValueWeb

import com.soyle.stories.common.Notifier
import com.soyle.stories.theme.usecases.addOppositionToValueWeb.AddOppositionToValueWeb
import com.soyle.stories.theme.usecases.addOppositionToValueWeb.OppositionAddedToValueWeb
import kotlin.coroutines.coroutineContext

class AddOppositionToValueWebNotifier : Notifier<AddOppositionToValueWeb.OutputPort>(), AddOppositionToValueWeb.OutputPort {

    override suspend fun addedOppositionToValueWeb(response: OppositionAddedToValueWeb) {
        notifyAll(coroutineContext) { it.addedOppositionToValueWeb(response) }
    }

}