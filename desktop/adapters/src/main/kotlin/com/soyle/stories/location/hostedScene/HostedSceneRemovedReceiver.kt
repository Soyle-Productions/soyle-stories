package com.soyle.stories.location.hostedScene

import com.soyle.stories.domain.location.events.HostedSceneRemoved

interface HostedSceneRemovedReceiver {

    suspend fun receiveHostedScenesRemoved(events: List<HostedSceneRemoved>)
    suspend fun receiveHostedSceneRemoved(event: HostedSceneRemoved)

}
