package com.soyle.stories.usecase.storyevent.rename

import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.storyevent.Successful
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.storyevent.StoryEventRepository

class RenameStoryEventUseCase(
  private val storyEventRepository: StoryEventRepository
) : RenameStoryEvent {

	override suspend fun invoke(
		storyEventId: StoryEvent.Id,
		name: NonBlankString,
		output: RenameStoryEvent.OutputPort
	) {

		val storyEvent = storyEventRepository.getStoryEventOrError(storyEventId)
		val update = storyEvent.withName(name)
		if (update is Successful) {
			storyEventRepository.updateStoryEvent(update.storyEvent)
			output.receiveRenameStoryEventResponse(RenameStoryEvent.ResponseModel(update.change))
		}
	}
}