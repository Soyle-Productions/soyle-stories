package com.soyle.stories.scene.usecases.includeCharacterInScene

import com.soyle.stories.storyevent.usecases.addCharacterToStoryEvent.AddCharacterToStoryEvent
import java.util.*

interface IncludeCharacterInScene {

	suspend operator fun invoke(response: AddCharacterToStoryEvent.ResponseModel, outputPort: OutputPort)

	class ResponseModel(val sceneId: UUID, val characterId: UUID)

	interface OutputPort {
		fun failedToIncludeCharacterInScene(failure: Exception)
		fun characterIncludedInScene(response: ResponseModel)
	}


}