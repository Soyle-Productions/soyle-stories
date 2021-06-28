package com.soyle.stories.usecase.scene.location.linkLocationToScene

import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.scene.events.LocationUsedInScene
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.SceneLocale

interface LinkLocationToScene {

	class RequestModel(val sceneId: Scene.Id, val locationId: Location.Id, val locale: SceneLocale)

	suspend operator fun invoke(request: RequestModel, output: OutputPort)

	class ResponseModel(val locationUsedInScene: LocationUsedInScene)

	interface OutputPort {
		suspend fun locationLinkedToScene(response: ResponseModel)
	}
}