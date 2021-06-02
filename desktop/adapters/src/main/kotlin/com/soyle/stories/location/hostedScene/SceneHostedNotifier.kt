package com.soyle.stories.location.hostedScene

import com.soyle.stories.common.Notifier
import com.soyle.stories.domain.location.events.SceneHostedAtLocation

class SceneHostedNotifier : Notifier<SceneHostedReceiver>(), SceneHostedReceiver {

    override suspend fun receiveSceneHostedAtLocation(event: SceneHostedAtLocation) {
        notifyAll { it.receiveSceneHostedAtLocation(event) }
    }

}
