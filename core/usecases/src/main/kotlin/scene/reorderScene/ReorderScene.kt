package com.soyle.stories.usecase.scene.reorderScene

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.SceneLocale
import com.soyle.stories.domain.scene.order.SceneOrderUpdate
import com.soyle.stories.domain.scene.order.SuccessfulSceneOrderUpdate
import com.soyle.stories.usecase.scene.listAllScenes.SceneItem
import java.util.*

interface ReorderScene {

	suspend operator fun invoke(sceneId: Scene.Id, newIndex: Int, output: OutputPort)

	class ResponseModel(
		val sceneOrderUpdate: SuccessfulSceneOrderUpdate<Nothing?>
	)

	fun interface OutputPort
	{
		fun sceneReordered(response: ResponseModel)
	}
}