package com.soyle.stories.location.hostedScene

import com.soyle.stories.domain.location.events.HostedSceneRenamed

interface HostedSceneRenamedReceiver {

    suspend fun receiveHostedScenesRenamed(events: List<HostedSceneRenamed>)
    suspend fun receiveHostedSceneRenamed(event: HostedSceneRenamed)

}
