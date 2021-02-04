package com.soyle.stories.scene.usecases.linkLocationToScene

import com.soyle.stories.scene.Locale
import java.util.*

interface LinkLocationToScene {

	class RequestModel(val sceneId: UUID, val locationId: UUID?, val locale: Locale)

	suspend operator fun invoke(request: RequestModel, output: OutputPort)

	class ResponseModel(val sceneId: UUID, val locationId: UUID?)

	interface OutputPort {
		fun failedToLinkLocationToScene(failure: Exception)
		fun locationLinkedToScene(response: ResponseModel)
	}
}