package com.soyle.stories.usecase.scene.getPotentialChangesFromDeletingScene

import com.soyle.stories.domain.scene.SceneLocale
import com.soyle.stories.usecase.scene.common.AffectedScene
import java.util.*

interface GetPotentialChangesFromDeletingScene {

	class RequestModel(val sceneId: UUID, val locale: SceneLocale)

	suspend operator fun invoke(request: RequestModel, output: OutputPort)

	class ResponseModel(val affectedScenes: List<AffectedScene>)

	interface OutputPort {
		fun receivePotentialChangesFromDeletingScene(response: ResponseModel)
		fun failedToGetPotentialChangesFromDeletingScene(failure: Exception)
	}
}