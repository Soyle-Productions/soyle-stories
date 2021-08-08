package com.soyle.stories.scene.inconsistencies

import com.soyle.stories.common.Notifier
import com.soyle.stories.usecase.scene.inconsistencies.SceneInconsistencies

class SceneInconsistenciesNotifier : Notifier<SceneInconsistenciesReceiver>(), SceneInconsistenciesReceiver {

    override suspend fun receiveSceneInconsistencies(sceneInconsistencies: SceneInconsistencies) {
        notifyAll { it.receiveSceneInconsistencies(sceneInconsistencies) }
    }
}