package com.soyle.stories.desktop.view.scene.sceneSetting.doubles

import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.scene.locationsInScene.replace.ReplaceSettingInSceneController
import kotlinx.coroutines.Job

class ReplaceSettingInSceneControllerDouble(
    private val onInvoke: (Scene.Id, Location.Id, Location.Id) -> Unit = { _, _, _ -> }
) : ReplaceSettingInSceneController {

    override fun replaceSettingInScene(sceneId: Scene.Id, settingId: Location.Id, replacementId: Location.Id): Job {
        onInvoke(sceneId, settingId, replacementId)
        return Job()
    }
}