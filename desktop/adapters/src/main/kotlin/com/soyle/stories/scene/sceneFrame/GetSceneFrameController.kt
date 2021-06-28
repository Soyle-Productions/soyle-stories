package com.soyle.stories.scene.sceneFrame

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.scene.sceneFrame.GetSceneFrame

interface GetSceneFrameController {

    fun getSceneFrame(sceneId: Scene.Id, output: GetSceneFrame.OutputPort)

}