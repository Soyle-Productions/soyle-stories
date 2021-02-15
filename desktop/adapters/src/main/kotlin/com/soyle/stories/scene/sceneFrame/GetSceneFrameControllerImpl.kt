package com.soyle.stories.scene.sceneFrame

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.scene.sceneFrame.GetSceneFrame

class GetSceneFrameControllerImpl(
    private val threadTransformer: ThreadTransformer,
    private val getSceneFrame: GetSceneFrame
) : GetSceneFrameController {

    override fun getSceneFrame(sceneId: Scene.Id, output: GetSceneFrame.OutputPort) {
        threadTransformer.async {
            getSceneFrame.invoke(sceneId, output)
        }
    }

}