package com.soyle.stories.usecase.scene.setMotivationForCharacterInScene

import com.soyle.stories.domain.scene.SceneLocale
import java.util.*

interface SetMotivationForCharacterInScene {

	class RequestModel(val sceneId: UUID, val characterId: UUID, val motivation: String?, val locale: SceneLocale)

	suspend operator fun invoke(request: RequestModel, output: OutputPort)

	class ResponseModel(val sceneId: UUID, val characterId: UUID, val motivation: String?)

	interface OutputPort {
		fun motivationSetForCharacterInScene(response: ResponseModel)
		fun failedToSetMotivationForCharacterInScene(failure: Exception)
	}
}