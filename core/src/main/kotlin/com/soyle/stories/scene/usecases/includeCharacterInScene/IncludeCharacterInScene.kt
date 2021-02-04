package com.soyle.stories.scene.usecases.includeCharacterInScene

import com.soyle.stories.scene.usecases.common.IncludedCharacterInScene
import com.soyle.stories.storyevent.usecases.addCharacterToStoryEvent.AddCharacterToStoryEvent
import com.soyle.stories.storyevent.usecases.addCharacterToStoryEvent.IncludedCharacterInStoryEvent
import java.util.*

interface IncludeCharacterInScene {

	suspend operator fun invoke(sceneId: UUID, characterId: UUID, outputPort: OutputPort)
	suspend operator fun invoke(response: AddCharacterToStoryEvent.ResponseModel, outputPort: OutputPort)

	class ResponseModel(
		val includedCharacterInScene: IncludedCharacterInScene,
		val includedCharacterInStoryEvent: IncludedCharacterInStoryEvent?
	)

	interface OutputPort {
		suspend fun characterIncludedInScene(response: ResponseModel)
	}


}