package com.soyle.stories.scene.locationsInScene.detectInconsistencies

import com.soyle.stories.scene.inconsistencies.SceneInconsistenciesReceiver
import com.soyle.stories.usecase.scene.inconsistencies.SceneInconsistencies
import com.soyle.stories.usecase.scene.location.detectInconsistencies.DetectInconsistenciesInSceneSettings

class DetectInconsistenciesInSceneSettingsOutput(
    private val sceneInconsistenciesReceiver: SceneInconsistenciesReceiver
) : DetectInconsistenciesInSceneSettings.OutputPort {

    override suspend fun receiveSceneInconsistencyReport(report: SceneInconsistencies) {
        sceneInconsistenciesReceiver.receiveSceneInconsistencies(report)
    }
}