package com.soyle.stories.usecase.storyevent.renameStoryEvent

import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.usecase.storyevent.StoryEventDoesNotExist
import com.soyle.stories.usecase.storyevent.StoryEventRepository
import java.util.*

class RenameStoryEventUseCase(
  private val storyEventRepository: StoryEventRepository
) : RenameStoryEvent {

	override suspend fun invoke(storyEventId: UUID, name: String, output: RenameStoryEvent.OutputPort) {
		val response = try {
			renameStoryEvent(storyEventId, name)
		} catch (s: Exception) {
			return output.receiveRenameStoryEventFailure(s)
		}
		output.receiveRenameStoryEventResponse(response)
	}

	private suspend fun renameStoryEvent(storyEventId: UUID, name: String): RenameStoryEvent.ResponseModel {
		val storyEvent = getStoryEvent(storyEventId)

		updateIfNeeded(storyEvent, name)

		return RenameStoryEvent.ResponseModel(storyEventId, name)
	}

	private suspend fun updateIfNeeded(storyEvent: StoryEvent, name: String) {
		if (storyEvent.name != name) {
			storyEventRepository.updateStoryEvent(storyEvent.withName(name))
		}
	}

	private suspend fun getStoryEvent(storyEventId: UUID) =
	  (storyEventRepository.getStoryEventById(StoryEvent.Id(storyEventId))
		?: throw StoryEventDoesNotExist(storyEventId))
}