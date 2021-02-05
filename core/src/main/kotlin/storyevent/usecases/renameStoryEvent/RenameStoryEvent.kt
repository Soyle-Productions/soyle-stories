package com.soyle.stories.storyevent.usecases.renameStoryEvent

import com.soyle.stories.storyevent.StoryEventException
import java.util.*

interface RenameStoryEvent {

	suspend operator fun invoke(storyEventId: UUID, name: String, output: OutputPort)

	class ResponseModel(val storyEventId: UUID, val newName: String)

	interface OutputPort {
		fun receiveRenameStoryEventFailure(failure: StoryEventException)
		fun receiveRenameStoryEventResponse(response: ResponseModel)
	}
}