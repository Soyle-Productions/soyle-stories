package com.soyle.stories.usecase.scene.linkLocationToScene

import com.soyle.stories.domain.scene.SceneLocale
import java.util.*

interface LinkLocationToScene {

	class RequestModel(val sceneId: UUID, val locationId: UUID?, val locale: SceneLocale)

	suspend operator fun invoke(request: RequestModel, output: OutputPort)

	class ResponseModel(val sceneId: UUID, val locationId: UUID?)

	interface OutputPort {
		fun failedToLinkLocationToScene(failure: Exception)
		fun locationLinkedToScene(response: ResponseModel)
	}
}