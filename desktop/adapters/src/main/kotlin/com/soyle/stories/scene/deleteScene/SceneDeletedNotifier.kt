package com.soyle.stories.scene.deleteScene

import com.soyle.stories.common.Notifier
import com.soyle.stories.domain.scene.Scene

class SceneDeletedNotifier : Notifier<SceneDeletedReceiver>(), SceneDeletedReceiver {

    override suspend fun receiveSceneDeleted(event: Scene.Id) {
        notifyAll { it.receiveSceneDeleted(event) }
    }

}
