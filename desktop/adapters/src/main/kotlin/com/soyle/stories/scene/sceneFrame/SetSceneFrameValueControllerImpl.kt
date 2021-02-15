package com.soyle.stories.scene.sceneFrame

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.SceneConflict
import com.soyle.stories.domain.scene.SceneResolution
import com.soyle.stories.usecase.scene.sceneFrame.SetSceneFrameValue

class SetSceneFrameValueControllerImpl(
    private val threadTransformer: ThreadTransformer,
    private val setSceneFrameValue: SetSceneFrameValue,
    private val setSceneFrameValueOutput: SetSceneFrameValue.OutputPort
) : SetSceneFrameValueController {

    override fun setSceneConflict(sceneId: Scene.Id, conflict: String) {
        threadTransformer.async {
            setSceneFrameValue(sceneId, SceneConflict(conflict), setSceneFrameValueOutput)
        }
    }

    override fun setSceneResolution(sceneId: Scene.Id, resolution: String) {
        threadTransformer.async {
            setSceneFrameValue(sceneId, SceneResolution(resolution), setSceneFrameValueOutput)
        }
    }

}