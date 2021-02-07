package com.soyle.stories.usecase.storyevent.renameStoryEvent

import java.util.*

interface RenameStoryEvent {

	suspend operator fun invoke(storyEventId: UUID, name: String, output: OutputPort)

	class ResponseModel(val storyEventId: UUID, val newName: String)

	interface OutputPort {
		fun receiveRenameStoryEventFailure(failure: Exception)
		fun receiveRenameStoryEventResponse(response: ResponseModel)
	}
}