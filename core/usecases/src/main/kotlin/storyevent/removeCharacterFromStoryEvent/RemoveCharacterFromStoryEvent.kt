package com.soyle.stories.usecase.storyevent.removeCharacterFromStoryEvent

import java.util.*

interface RemoveCharacterFromStoryEvent {

	suspend operator fun invoke(storyEventId: UUID, characterId: UUID, output: OutputPort)

	suspend fun removeCharacterFromAllStoryEvents(characterId: UUID, output: OutputPort)

	class ResponseModel(val storyEventId: UUID, val removedCharacterId: UUID)

	interface OutputPort {
		fun receiveRemoveCharacterFromStoryEventFailure(failure: Exception)
		fun receiveRemoveCharacterFromStoryEventResponse(response: ResponseModel)
	}

}