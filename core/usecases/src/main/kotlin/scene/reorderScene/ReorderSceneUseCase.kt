package com.soyle.stories.usecase.scene.reorderScene

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.order.SceneOrderUpdate
import com.soyle.stories.usecase.scene.SceneRepository

class ReorderSceneUseCase(
  private val sceneRepository: SceneRepository
) : ReorderScene {

	override suspend fun invoke(sceneId: Scene.Id, newIndex: Int, output: ReorderScene.OutputPort) {
		val scene = sceneRepository.getSceneOrError(sceneId.uuid)
		val sceneOrder = sceneRepository.getSceneIdsInOrder(scene.projectId)!!
		val update = sceneOrder.withScene(scene.id)!!.movedTo(newIndex)
		when (update) {
			is SceneOrderUpdate.UnSuccessful -> update.reason?.let { throw it }
			is SceneOrderUpdate.Successful -> {
				sceneRepository.updateSceneOrder(update.sceneOrder)
				output.sceneReordered(ReorderScene.ResponseModel(update))
			}
		}
	}
}