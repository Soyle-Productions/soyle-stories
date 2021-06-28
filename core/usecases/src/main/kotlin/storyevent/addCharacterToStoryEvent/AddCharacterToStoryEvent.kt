package com.soyle.stories.usecase.storyevent.addCharacterToStoryEvent

import java.util.*

interface AddCharacterToStoryEvent {

	suspend operator fun invoke(storyEventId: UUID, characterId: UUID, output: OutputPort)

	class ResponseModel(val storyEventId: UUID, val characterId: UUID)

	interface OutputPort {
		fun receiveAddCharacterToStoryEventFailure(failure: Exception)
		fun receiveAddCharacterToStoryEventResponse(response: ResponseModel)
	}

}