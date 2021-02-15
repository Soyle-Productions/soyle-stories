package com.soyle.stories.usecase.scene.sceneFrame

import com.soyle.stories.domain.scene.Scene

interface GetSceneFrame {

    suspend operator fun invoke(sceneId: Scene.Id, output: OutputPort)

    class ResponseModel(
        val sceneId: Scene.Id,
        val sceneConflict: String,
        val sceneResolution: String
    )

    interface OutputPort {
        suspend fun receiveSceneFrame(response: ResponseModel)
    }

}