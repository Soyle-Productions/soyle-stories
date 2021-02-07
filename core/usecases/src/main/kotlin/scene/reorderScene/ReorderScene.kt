package com.soyle.stories.usecase.scene.reorderScene

import com.soyle.stories.domain.scene.SceneLocale
import com.soyle.stories.usecase.scene.listAllScenes.SceneItem
import java.util.*

interface ReorderScene {

	class RequestModel(val sceneId: UUID, val newIndex: Int, val locale: SceneLocale)

	suspend operator fun invoke(request: RequestModel, output: OutputPort)

	class ResponseModel(val scene: SceneItem, val oldIndex: Int, val updatedScenes: List<SceneItem>)

	interface OutputPort
	{
		fun failedToReorderScene(failure: Exception)
		fun sceneReordered(response: ResponseModel)
	}
}