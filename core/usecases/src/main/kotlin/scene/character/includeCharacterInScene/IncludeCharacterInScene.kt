package com.soyle.stories.usecase.scene.character.includeCharacterInScene

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.scene.common.IncludedCharacterInScene
import com.soyle.stories.usecase.storyevent.addCharacterToStoryEvent.AddCharacterToStoryEvent
import com.soyle.stories.usecase.storyevent.addCharacterToStoryEvent.IncludedCharacterInStoryEvent
import java.util.*

interface IncludeCharacterInScene {

	suspend operator fun invoke(sceneId: UUID, characterId: UUID, outputPort: OutputPort)
	suspend operator fun invoke(response: AddCharacterToStoryEvent.ResponseModel, outputPort: OutputPort)

	class ResponseModel(
		val sceneId: Scene.Id,
		val includedCharacterInScene: IncludedCharacterInScene,
		val includedCharacterInStoryEvent: IncludedCharacterInStoryEvent?
	)

	interface OutputPort {
		suspend fun characterIncludedInScene(response: ResponseModel)
	}


}