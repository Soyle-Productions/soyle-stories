package com.soyle.stories.usecase.scene.character.removeCharacterFromScene

import com.soyle.stories.domain.scene.SceneLocale
import java.util.*

interface RemoveCharacterFromScene {

	class RequestModel internal constructor(val sceneId: UUID?, val storyEventId: UUID?, val characterId: UUID, val locale: SceneLocale)

	suspend fun removeCharacterFromScene(sceneId: UUID, locale: SceneLocale, characterId: UUID, output: OutputPort) =
	  invoke(RequestModel(sceneId, null, characterId, locale), output)
	suspend fun removeCharacterFromSceneWithStoryEventId(storyEventId: UUID, locale: SceneLocale, characterId: UUID, output: OutputPort) =
	  invoke(RequestModel(null, storyEventId, characterId, locale), output)

	suspend operator fun invoke(request: RequestModel, output: OutputPort)

	class ResponseModel(val sceneId: UUID, val characterId: UUID)

	interface OutputPort {
		fun failedToRemoveCharacterFromScene(failure: Exception)
		suspend fun characterRemovedFromScene(response: ResponseModel)
	}
}