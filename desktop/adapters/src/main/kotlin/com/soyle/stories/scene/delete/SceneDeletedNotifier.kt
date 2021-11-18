package com.soyle.stories.scene.delete

import com.soyle.stories.common.Notifier
import com.soyle.stories.domain.scene.events.SceneRemoved

class SceneDeletedNotifier : Notifier<SceneDeletedReceiver>(), SceneDeletedReceiver {

    override suspend fun receiveSceneDeleted(event: SceneRemoved) {
        notifyAll { it.receiveSceneDeleted(event) }
    }

}
