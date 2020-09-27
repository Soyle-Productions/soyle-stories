package com.soyle.stories.scene.usecases.getSceneDetails

import com.soyle.stories.scene.Locale
import com.soyle.stories.scene.usecases.common.IncludedCharacterInScene
import java.util.*

interface GetSceneDetails {

	class RequestModel(val sceneId: UUID, val locale: Locale)

	suspend operator fun invoke(request: RequestModel, output: OutputPort)

	class ResponseModel(val sceneId: UUID, val storyEventId: UUID, val locationId: UUID?, val characters: List<IncludedCharacterInScene>)

	interface OutputPort {
		fun failedToGetSceneDetails(failure: Exception)
		fun sceneDetailsRetrieved(response: ResponseModel)
	}

}