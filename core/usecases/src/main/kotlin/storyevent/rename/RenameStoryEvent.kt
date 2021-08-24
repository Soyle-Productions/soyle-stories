package com.soyle.stories.usecase.storyevent.rename

import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.storyevent.events.StoryEventRenamed
import com.soyle.stories.domain.validation.NonBlankString

interface RenameStoryEvent {

	suspend operator fun invoke(storyEventId: StoryEvent.Id, name: NonBlankString, output: OutputPort)

	class ResponseModel(val storyEventRenamed: StoryEventRenamed)

	fun interface OutputPort {
		fun receiveRenameStoryEventResponse(response: ResponseModel)
	}
}