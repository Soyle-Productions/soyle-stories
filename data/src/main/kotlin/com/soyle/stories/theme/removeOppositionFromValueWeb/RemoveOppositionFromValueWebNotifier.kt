package com.soyle.stories.theme.removeOppositionFromValueWeb

import com.soyle.stories.common.Notifier
import com.soyle.stories.theme.usecases.removeOppositionFromValueWeb.OppositionRemovedFromValueWeb
import com.soyle.stories.theme.usecases.removeOppositionFromValueWeb.RemoveOppositionFromValueWeb
import kotlin.coroutines.coroutineContext

class RemoveOppositionFromValueWebNotifier : Notifier<RemoveOppositionFromValueWeb.OutputPort>(), RemoveOppositionFromValueWeb.OutputPort {

    override suspend fun removedOppositionFromValueWeb(response: OppositionRemovedFromValueWeb) {
        notifyAll(coroutineContext) { it.removedOppositionFromValueWeb(response) }
    }
}