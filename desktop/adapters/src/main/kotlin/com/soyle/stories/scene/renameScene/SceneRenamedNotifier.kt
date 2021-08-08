package com.soyle.stories.scene.renameScene

import com.soyle.stories.common.Notifier
import com.soyle.stories.domain.scene.events.SceneRenamed

class SceneRenamedNotifier : Notifier<SceneRenamedReceiver>(), SceneRenamedReceiver {

    override suspend fun receiveSceneRenamed(event: SceneRenamed) {
        notifyAll { it.receiveSceneRenamed(event) }
    }

}
