package com.soyle.stories.scene.usecases.getPotentialChangesFromDeletingScene

import com.soyle.stories.scene.Locale
import java.util.*

interface GetPotentialChangesFromDeletingScene {

	class RequestModel(val sceneId: UUID, val locale: Locale)

	suspend operator fun invoke(request: RequestModel, output: OutputPort)

	class ResponseModel(val affectedScenes: List<AffectedScene>)
	class AffectedScene(val sceneId: UUID, val sceneName: String, val characters: List<AffectedCharacter>)
	class AffectedCharacter(val characterId: UUID, val characterName: String, val currentMotivation: String, val potentialMotivation: String)

	interface OutputPort {
		fun receivePotentialChangesFromDeletingScene(response: ResponseModel)
		fun failedToGetPotentialChangesFromDeletingScene(failure: Exception)
	}
}