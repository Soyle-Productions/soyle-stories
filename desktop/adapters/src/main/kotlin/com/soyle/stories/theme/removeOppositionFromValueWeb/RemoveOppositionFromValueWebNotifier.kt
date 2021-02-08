package com.soyle.stories.theme.removeOppositionFromValueWeb

import com.soyle.stories.common.Notifier
import com.soyle.stories.usecase.theme.removeOppositionFromValueWeb.OppositionRemovedFromValueWeb
import com.soyle.stories.usecase.theme.removeOppositionFromValueWeb.RemoveOppositionFromValueWeb

class RemoveOppositionFromValueWebNotifier : Notifier<RemoveOppositionFromValueWeb.OutputPort>(), RemoveOppositionFromValueWeb.OutputPort {

    override suspend fun removedOppositionFromValueWeb(response: OppositionRemovedFromValueWeb) {
        notifyAll { it.removedOppositionFromValueWeb(response) }
    }
}