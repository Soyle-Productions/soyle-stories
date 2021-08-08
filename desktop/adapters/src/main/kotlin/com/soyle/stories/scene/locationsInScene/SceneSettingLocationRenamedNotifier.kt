package com.soyle.stories.scene.locationsInScene

import com.soyle.stories.common.Notifier
import com.soyle.stories.domain.scene.events.SceneSettingLocationRenamed

class SceneSettingLocationRenamedNotifier : Notifier<SceneSettingLocationRenamedReceiver>(),
    SceneSettingLocationRenamedReceiver {

    override suspend fun receiveSceneSettingLocaitonsRenamed(events: List<SceneSettingLocationRenamed>) {
        notifyAll { it.receiveSceneSettingLocaitonsRenamed(events) }
    }

    override suspend fun receiveSceneSettingLocationRenamed(sceneSettingLocationRenamed: SceneSettingLocationRenamed) {
        notifyAll { it.receiveSceneSettingLocationRenamed(sceneSettingLocationRenamed) }
    }
}
