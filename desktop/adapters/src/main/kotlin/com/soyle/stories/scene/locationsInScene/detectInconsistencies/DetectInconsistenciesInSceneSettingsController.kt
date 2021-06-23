package com.soyle.stories.scene.locationsInScene.detectInconsistencies

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.scene.location.detectInconsistencies.DetectInconsistenciesInSceneSettings
import kotlinx.coroutines.Job

interface DetectInconsistenciesInSceneSettingsController {

    companion object {
        operator fun invoke(
            threadTransformer: ThreadTransformer,
            detectInconsistencies: DetectInconsistenciesInSceneSettings,
            detectInconsistenciesOutput: DetectInconsistenciesInSceneSettings.OutputPort
        ): DetectInconsistenciesInSceneSettingsController = object : DetectInconsistenciesInSceneSettingsController {
            override fun detectInconsistencies(sceneId: Scene.Id): Job {
                return threadTransformer.async {
                    detectInconsistencies.invoke(sceneId, detectInconsistenciesOutput)
                }
            }
        }
    }

    fun detectInconsistencies(sceneId: Scene.Id): Job

}