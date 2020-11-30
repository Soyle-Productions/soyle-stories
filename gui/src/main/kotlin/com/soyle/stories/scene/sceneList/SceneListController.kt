package com.soyle.stories.scene.sceneList

import com.soyle.stories.common.NonBlankString
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.layout.openTool.OpenToolController
import com.soyle.stories.scene.renameScene.RenameSceneController
import com.soyle.stories.scene.reorderScene.ReorderSceneController
import com.soyle.stories.scene.usecases.listAllScenes.ListAllScenes

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

	override fun openSceneDetails(sceneId: String) {
		openToolController.openSceneDetailsTool(sceneId)
	}

	override fun renameScene(sceneId: String, newName: NonBlankString) {
		renameSceneController.renameScene(sceneId, newName)
	}

	override fun reorderScene(sceneId: String, newIndex: Int) {
		reorderSceneController.reorderScene(sceneId, newIndex)
	}
}