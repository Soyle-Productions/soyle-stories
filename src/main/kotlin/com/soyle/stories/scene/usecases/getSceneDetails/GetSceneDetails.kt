package com.soyle.stories.scene.usecases.getSceneDetails

import com.soyle.stories.scene.Locale
import java.util.*

interface GetSceneDetails {

	class RequestModel(val sceneId: UUID, val locale: Locale)

	suspend operator fun invoke(request: RequestModel, output: OutputPort)

	class ResponseModel(val sceneId: UUID, val locationId: UUID?, val characters: List<IncludedCharacterDetails>)
	class IncludedCharacterDetails(val characterId: UUID, val characterName: String, val motivation: String?, val inheritedMotivation: InheritedMotivation?)
	class InheritedMotivation(val sceneId: UUID, val sceneName: String, val motivation: String)

	interface OutputPort {
		fun failedToGetSceneDetails(failure: Exception)
		fun sceneDetailsRetrieved(response: ResponseModel)
	}

}