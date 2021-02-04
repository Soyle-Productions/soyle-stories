package com.soyle.stories.scene.deleteSceneRamifications

import com.soyle.stories.common.LocaleManager
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.layout.closeTool.CloseToolController
import com.soyle.stories.layout.openTool.OpenToolController
import com.soyle.stories.layout.usecases.closeTool.CloseTool
import com.soyle.stories.scene.deleteScene.DeleteSceneController
import com.soyle.stories.scene.usecases.getPotentialChangesFromDeletingScene.GetPotentialChangesFromDeletingScene
import java.util.*

class DeleteSceneRamificationsController(
  sceneId: String,
  private val toolId: String,
  private val threadTransformer: ThreadTransformer,
  private val localeManager: LocaleManager,
  private val getPotentialChangesFromDeletingScene: GetPotentialChangesFromDeletingScene,
  private val getPotentialChangesFromDeletingSceneOutputPort: GetPotentialChangesFromDeletingScene.OutputPort,
  private val deleteSceneController: DeleteSceneController,
  private val closeToolController: CloseToolController
) : DeleteSceneRamificationsViewListener {

	private val sceneId = UUID.fromString(sceneId)

	override fun getValidState() {
		threadTransformer.async {
			getPotentialChangesFromDeletingScene.invoke(
			  GetPotentialChangesFromDeletingScene.RequestModel(sceneId, localeManager.getCurrentLocale()),
			  getPotentialChangesFromDeletingSceneOutputPort
			)
		}
	}

	override fun deleteScene(sceneId: String) {
		deleteSceneController.deleteScene(sceneId)
	}

	override fun cancel() {
		closeToolController.closeTool(toolId)
	}

}