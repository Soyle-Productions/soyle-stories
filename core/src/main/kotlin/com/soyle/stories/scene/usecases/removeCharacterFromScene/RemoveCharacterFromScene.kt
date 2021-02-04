package com.soyle.stories.scene.usecases.removeCharacterFromScene

import com.soyle.stories.scene.Locale
import java.util.*

interface RemoveCharacterFromScene {

	class RequestModel internal constructor(val sceneId: UUID?, val storyEventId: UUID?, val characterId: UUID, val locale: Locale)

	suspend fun removeCharacterFromScene(sceneId: UUID, locale: Locale, characterId: UUID, output: OutputPort) =
	  invoke(RequestModel(sceneId, null, characterId, locale), output)
	suspend fun removeCharacterFromSceneWithStoryEventId(storyEventId: UUID, locale: Locale, characterId: UUID, output: OutputPort) =
	  invoke(RequestModel(null, storyEventId, characterId, locale), output)

	suspend operator fun invoke(request: RequestModel, output: OutputPort)

	class ResponseModel(val sceneId: UUID, val characterId: UUID)

	interface OutputPort {
		fun failedToRemoveCharacterFromScene(failure: Exception)
		fun characterRemovedFromScene(response: ResponseModel)
	}
}