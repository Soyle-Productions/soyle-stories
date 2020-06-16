package com.soyle.stories.layout.removeToolsWithId

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.layout.usecases.removeToolsWithId.RemoveToolsWithId
import com.soyle.stories.scene.SceneException
import com.soyle.stories.scene.usecases.deleteScene.DeleteScene

class RemoveToolsWithIdController(
    private val threadTransformer: ThreadTransformer,
    private val removeToolsWithId: RemoveToolsWithId,
    private val removeToolsWithIdOutputPort: RemoveToolsWithId.OutputPort
) : DeleteScene.OutputPort {

    override fun receiveDeleteSceneResponse(responseModel: DeleteScene.ResponseModel) {
        threadTransformer.async {
            removeToolsWithId.invoke(
                responseModel.sceneId,
                removeToolsWithIdOutputPort
            )
        }
    }

    override fun receiveDeleteSceneFailure(failure: SceneException) {}
}