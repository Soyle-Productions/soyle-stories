package com.soyle.stories.scene.deleteSceneRamifications

import com.soyle.stories.common.LocaleManager
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.layout.closeTool.CloseToolController
import com.soyle.stories.scene.delete.DeleteSceneController
import com.soyle.stories.usecase.scene.delete.GetPotentialChangesFromDeletingScene
import java.util.*

class DeleteSceneRamificationsController(
    private val sceneId: Scene.Id,
    private val toolId: String,
    private val threadTransformer: ThreadTransformer,
    private val localeManager: LocaleManager,
    private val getPotentialChangesFromDeletingScene: GetPotentialChangesFromDeletingScene,
    private val getPotentialChangesFromDeletingSceneOutputPort: GetPotentialChangesFromDeletingScene.OutputPort,
    private val deleteSceneController: DeleteSceneController,
    private val closeToolController: CloseToolController
) : DeleteSceneRamificationsViewListener {

	override fun getValidState() {
		threadTransformer.async {
			getPotentialChangesFromDeletingScene.invoke(
			  sceneId,
			  getPotentialChangesFromDeletingSceneOutputPort
			)
		}
	}

	override fun deleteScene(sceneId: Scene.Id) {
		deleteSceneController.deleteScene(sceneId)
	}

	override fun cancel() {
		closeToolController.closeTool(toolId)
	}

}