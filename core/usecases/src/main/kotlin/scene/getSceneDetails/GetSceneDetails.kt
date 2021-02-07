package com.soyle.stories.usecase.scene.getSceneDetails

import com.soyle.stories.domain.scene.SceneLocale
import com.soyle.stories.usecase.scene.common.IncludedCharacterInScene
import java.util.*

interface GetSceneDetails {

	class RequestModel(val sceneId: UUID, val locale: SceneLocale)

	suspend operator fun invoke(request: RequestModel, output: OutputPort)

	class ResponseModel(val sceneId: UUID, val storyEventId: UUID, val locationId: UUID?, val characters: List<IncludedCharacterInScene>)

	interface OutputPort {
		fun failedToGetSceneDetails(failure: Exception)
		fun sceneDetailsRetrieved(response: ResponseModel)
	}

}