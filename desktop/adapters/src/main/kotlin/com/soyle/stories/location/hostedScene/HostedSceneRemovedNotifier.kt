package com.soyle.stories.location.hostedScene

import com.soyle.stories.common.Notifier
import com.soyle.stories.domain.location.events.HostedSceneRemoved

class HostedSceneRemovedNotifier : Notifier<HostedSceneRemovedReceiver>(), HostedSceneRemovedReceiver {

    override suspend fun receiveHostedScenesRemoved(events: List<HostedSceneRemoved>) {
        notifyAll { it.receiveHostedScenesRemoved(events) }
    }

    override suspend fun receiveHostedSceneRemoved(event: HostedSceneRemoved) {
        notifyAll { it.receiveHostedSceneRemoved(event) }
    }
}
