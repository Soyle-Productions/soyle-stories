package com.soyle.stories.desktop.view.scene.sceneSetting.doubles

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.scene.locationsInScene.detectInconsistencies.DetectInconsistenciesInSceneSettingsController
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.Job

class DetectInconsistenciesInSceneSettingsControllerDouble(
    var onInvoke: (Scene.Id) -> Unit = {},
    var job: CompletableJob = Job()
) : DetectInconsistenciesInSceneSettingsController {

    override fun detectInconsistencies(sceneId: Scene.Id): Job {
        onInvoke(sceneId)
        return job
    }
}