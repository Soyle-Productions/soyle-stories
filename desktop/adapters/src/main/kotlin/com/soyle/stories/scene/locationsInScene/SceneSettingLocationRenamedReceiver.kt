package com.soyle.stories.scene.locationsInScene

import com.soyle.stories.domain.scene.events.SceneSettingLocationRenamed

interface SceneSettingLocationRenamedReceiver {

    suspend fun receiveSceneSettingLocaitonsRenamed(events: List<SceneSettingLocationRenamed>)
    suspend fun receiveSceneSettingLocationRenamed(sceneSettingLocationRenamed: SceneSettingLocationRenamed)

}
