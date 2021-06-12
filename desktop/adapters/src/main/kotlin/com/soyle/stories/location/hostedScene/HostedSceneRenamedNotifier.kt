package com.soyle.stories.location.hostedScene

import com.soyle.stories.common.Notifier
import com.soyle.stories.domain.location.events.HostedSceneRenamed

class HostedSceneRenamedNotifier : Notifier<HostedSceneRenamedReceiver>(), HostedSceneRenamedReceiver {

    override suspend fun receiveHostedScenesRenamed(events: List<HostedSceneRenamed>) {
        notifyAll { it.receiveHostedScenesRenamed(events) }
    }

    override suspend fun receiveHostedSceneRenamed(event: HostedSceneRenamed) {
        notifyAll { it.receiveHostedSceneRenamed(event) }
    }

}
