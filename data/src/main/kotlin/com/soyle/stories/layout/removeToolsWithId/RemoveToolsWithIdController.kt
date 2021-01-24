package com.soyle.stories.layout.removeToolsWithId

import com.soyle.stories.characterarc.usecases.deleteCharacterArc.DeletedCharacterArc
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.layout.usecases.removeToolsWithId.RemoveToolsWithId
import com.soyle.stories.scene.SceneException
import com.soyle.stories.scene.usecases.deleteScene.DeleteScene
import com.soyle.stories.theme.usecases.deleteTheme.DeleteTheme
import com.soyle.stories.theme.usecases.deleteTheme.DeletedTheme

class RemoveToolsWithIdController(
    private val threadTransformer: ThreadTransformer,
    private val removeToolsWithId: RemoveToolsWithId,
    private val removeToolsWithIdOutputPort: RemoveToolsWithId.OutputPort
) : DeleteScene.OutputPort, DeleteTheme.OutputPort {

    override fun receiveDeleteSceneResponse(responseModel: DeleteScene.ResponseModel) {
        threadTransformer.async {
            removeToolsWithId.invoke(
                responseModel.sceneId,
                removeToolsWithIdOutputPort
            )
        }
    }

    override fun themeDeleted(response: DeletedTheme) {
        threadTransformer.async {
            removeToolsWithId.invoke(
                response.themeId,
                removeToolsWithIdOutputPort
            )
        }
    }

    override suspend fun characterArcsDeleted(response: List<DeletedCharacterArc>) {
        // do nothing
    }

    override fun receiveDeleteSceneFailure(failure: SceneException) {}
}