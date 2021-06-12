package com.soyle.stories.layout.removeToolsWithId

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.layout.usecases.removeToolsWithId.RemoveToolsWithId
import com.soyle.stories.scene.deleteScene.SceneDeletedReceiver
import com.soyle.stories.theme.deleteTheme.ThemeDeletedReceiver
import com.soyle.stories.usecase.theme.deleteTheme.DeletedTheme

class RemoveToolsWithIdController(
    private val removeToolsWithId: RemoveToolsWithId,
    private val removeToolsWithIdOutputPort: RemoveToolsWithId.OutputPort
) : SceneDeletedReceiver, ThemeDeletedReceiver {

    override suspend fun receiveSceneDeleted(event: Scene.Id) {
        removeToolsWithId.invoke(
            event.uuid,
            removeToolsWithIdOutputPort
        )
    }

    override suspend fun receiveDeletedTheme(deletedTheme: DeletedTheme) {
        removeToolsWithId.invoke(
            deletedTheme.themeId,
            removeToolsWithIdOutputPort
        )
    }
}
