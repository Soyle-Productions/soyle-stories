package com.soyle.stories.usecase.scene.deleteScene

import com.soyle.stories.domain.location.events.HostedSceneRemoved
import com.soyle.stories.domain.scene.SceneLocale
import java.util.UUID

interface DeleteScene {

	suspend operator fun invoke(sceneId: UUID, locale: SceneLocale, output: OutputPort)

	class ResponseModel(val sceneId: UUID, val hostedScenesRemoved: List<HostedSceneRemoved>)

	interface OutputPort {
		fun receiveDeleteSceneFailure(failure: Exception)
		fun receiveDeleteSceneResponse(responseModel: ResponseModel)
	}
}
