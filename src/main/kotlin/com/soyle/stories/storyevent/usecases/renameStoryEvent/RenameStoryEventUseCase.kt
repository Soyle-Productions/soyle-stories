package com.soyle.stories.storyevent.usecases.renameStoryEvent

import com.soyle.stories.entities.StoryEvent
import com.soyle.stories.storyevent.StoryEventDoesNotExist
import com.soyle.stories.storyevent.StoryEventException
import com.soyle.stories.storyevent.repositories.StoryEventRepository
import java.util.*

class RenameStoryEventUseCase(
  private val storyEventRepository: StoryEventRepository
) : RenameStoryEvent {

	override suspend fun invoke(storyEventId: UUID, name: String, output: RenameStoryEvent.OutputPort) {
		val response = try {
			renameStoryEvent(storyEventId, name)
		} catch (s: StoryEventException) {
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