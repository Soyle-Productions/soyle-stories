package com.soyle.stories.scene.usecases.reorderScene

import com.soyle.stories.scene.Locale
import com.soyle.stories.scene.usecases.listAllScenes.SceneItem
import java.util.*

interface ReorderScene {

	class RequestModel(val sceneId: UUID, val newIndex: Int, val locale: Locale)

	suspend operator fun invoke(request: RequestModel, output: OutputPort)

	class ResponseModel(val scene: SceneItem, val oldIndex: Int, val updatedScenes: List<SceneItem>)

	interface OutputPort
	{
		fun failedToReorderScene(failure: Exception)
		fun sceneReordered(response: ResponseModel)
	}
}