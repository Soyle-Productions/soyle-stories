package com.soyle.stories.scene.usecases.getPotentialChangesFromDeletingScene

import com.soyle.stories.scene.Locale
import com.soyle.stories.scene.usecases.common.AffectedScene
import java.util.*

interface GetPotentialChangesFromDeletingScene {

	class RequestModel(val sceneId: UUID, val locale: Locale)

	suspend operator fun invoke(request: RequestModel, output: OutputPort)

	class ResponseModel(val affectedScenes: List<AffectedScene>)

	interface OutputPort {
		fun receivePotentialChangesFromDeletingScene(response: ResponseModel)
		fun failedToGetPotentialChangesFromDeletingScene(failure: Exception)
	}
}