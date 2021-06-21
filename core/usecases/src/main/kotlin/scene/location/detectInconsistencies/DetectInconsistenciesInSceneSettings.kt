package com.soyle.stories.usecase.scene.location.detectInconsistencies

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.scene.inconsistencies.SceneInconsistencies

interface DetectInconsistenciesInSceneSettings {

    suspend operator fun invoke(sceneId: Scene.Id, output: OutputPort)

    fun interface OutputPort {
        suspend fun receiveSceneInconsistencyReport(report: SceneInconsistencies)
    }

}