package com.soyle.stories.usecase.scene.reorderScene

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.order.SuccessfulSceneOrderUpdate

interface ReorderScene {

	suspend operator fun invoke(sceneId: Scene.Id, newIndex: Int, output: OutputPort)

	class ResponseModel(
		val sceneOrderUpdate: SuccessfulSceneOrderUpdate<Nothing?>
	)

	fun interface OutputPort
	{
		suspend fun sceneReordered(response: ResponseModel)
	}
}