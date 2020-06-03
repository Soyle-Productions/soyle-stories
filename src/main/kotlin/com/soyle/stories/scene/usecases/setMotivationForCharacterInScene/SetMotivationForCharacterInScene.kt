package com.soyle.stories.scene.usecases.setMotivationForCharacterInScene

import com.soyle.stories.scene.Locale
import java.util.*

interface SetMotivationForCharacterInScene {

	class RequestModel(val sceneId: UUID, val characterId: UUID, val motivation: String?, val locale: Locale)

	suspend operator fun invoke(request: RequestModel, output: OutputPort)

	class ResponseModel(val sceneId: UUID, val characterId: UUID, val motivation: String?)

	interface OutputPort {
		fun motivationSetForCharacterInScene(response: ResponseModel)
		fun failedToSetMotivationForCharacterInScene(failure: Exception)
	}
}