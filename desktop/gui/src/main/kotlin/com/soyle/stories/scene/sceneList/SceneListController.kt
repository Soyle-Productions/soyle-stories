package com.soyle.stories.scene.sceneList

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.prose.Prose
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.layout.openTool.OpenToolController
import com.soyle.stories.scene.items.SceneItemViewModel
import com.soyle.stories.scene.renameScene.RenameSceneController
import com.soyle.stories.scene.reorderScene.ReorderSceneController
import com.soyle.stories.usecase.scene.listAllScenes.ListAllScenes
import com.soyle.stories.usecase.scene.listAllScenes.SceneItem
import java.util.*

class SceneListController(
    private val threadTransformer: ThreadTransformer,
    private val listAllScenes: ListAllScenes,
    private val listAllScenesOutputPort: ListAllScenes.OutputPort,
    private val renameSceneController: RenameSceneController,
    private val openToolController: OpenToolController,
    private val reorderSceneController: ReorderSceneController
) : SceneListViewListener {
    override fun getValidState() {
        threadTransformer.async {
            listAllScenes.invoke(listAllScenesOutputPort)
        }
    }

    override fun editScene(sceneId: String, proseId: Prose.Id) {
        openToolController.openSceneEditor(sceneId, proseId)
    }

    override fun trackCharacters(sceneItem: SceneItemViewModel) {
        openToolController.scene.openSceneCharacters(
            SceneItem(
                UUID.fromString(sceneItem.id),
                sceneItem.proseId,
                sceneItem.name,
                sceneItem.index
            )
        )
    }

    override fun trackLocations(sceneItem: SceneItemViewModel) {
        openToolController.scene.openSceneLocations(
            SceneItem(
                UUID.fromString(sceneItem.id),
                sceneItem.proseId,
                sceneItem.name,
                sceneItem.index
            )
        )
    }

    override fun trackSymbols(sceneItem: SceneItemViewModel) {
        openToolController.openSymbolsInScene(
            SceneItem(
                UUID.fromString(sceneItem.id),
                sceneItem.proseId,
                sceneItem.name,
                sceneItem.index
            )
        )
    }

    override fun renameScene(sceneId: String, newName: NonBlankString) {
        renameSceneController.renameScene(sceneId, newName)
    }

    override fun reorderScene(sceneId: String, newIndex: Int) {
        reorderSceneController.reorderScene(sceneId, newIndex)
    }
}