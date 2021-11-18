package com.soyle.stories.usecase.scene.deleteScene

import com.soyle.stories.domain.location.events.HostedSceneRemoved
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.SceneLocale
import com.soyle.stories.domain.scene.events.SceneRemoved
import java.util.UUID

interface DeleteScene {

	suspend operator fun invoke(sceneId: Scene.Id, output: OutputPort)

	class ResponseModel(val sceneRemoved: SceneRemoved, val hostedScenesRemoved: List<HostedSceneRemoved>)

	fun interface OutputPort {
		suspend fun receiveDeleteSceneResponse(responseModel: ResponseModel)
	}
}
