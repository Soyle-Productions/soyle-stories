package com.soyle.stories.scene.inconsistencies

import com.soyle.stories.usecase.scene.inconsistencies.SceneInconsistencies

interface SceneInconsistenciesReceiver {
    suspend fun receiveSceneInconsistencies(sceneInconsistencies: SceneInconsistencies)

}