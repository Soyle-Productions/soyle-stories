package com.soyle.stories.layout.removeToolsWithId

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.layout.usecases.removeToolsWithId.RemoveToolsWithId
import com.soyle.stories.scene.SceneException
import com.soyle.stories.scene.usecases.deleteScene.DeleteScene
import com.soyle.stories.theme.deleteTheme.ThemeDeletedReceiver
import com.soyle.stories.theme.usecases.deleteTheme.DeletedTheme

class RemoveToolsWithIdController(
    private val threadTransformer: ThreadTransformer,
    private val removeToolsWithId: RemoveToolsWithId,
    private val removeToolsWithIdOutputPort: RemoveToolsWithId.OutputPort
) : DeleteScene.OutputPort, ThemeDeletedReceiver {

    override fun receiveDeleteSceneResponse(responseModel: DeleteScene.ResponseModel) {
        threadTransformer.async {
            removeToolsWithId.invoke(
                responseModel.sceneId,
                removeToolsWithIdOutputPort
            )
        }
    }

    override suspend fun receiveDeletedTheme(deletedTheme: DeletedTheme) {
        removeToolsWithId.invoke(
            deletedTheme.themeId,
            removeToolsWithIdOutputPort
        )
    }

    override fun receiveDeleteSceneFailure(failure: SceneException) {}
}