package com.soyle.stories.storyevent.usecases.getStoryEventDetails

import com.soyle.stories.entities.Character
import com.soyle.stories.entities.StoryEvent
import com.soyle.stories.storyevent.StoryEventDoesNotExist
import com.soyle.stories.storyevent.StoryEventException
import com.soyle.stories.storyevent.repositories.StoryEventRepository
import java.util.*

class GetStoryEventDetailsUseCase(
  private val storyEventRepository: StoryEventRepository
) : GetStoryEventDetails {

	override suspend fun invoke(storyEventId: UUID, output: GetStoryEventDetails.OutputPort) {
		val response = try {
			val storyEvent = getStoryEvent(storyEventId)

			responseModel(storyEvent)
		} catch (e: StoryEventException) {
			return output.receiveGetStoryEventDetailsFailure(e)
		}
		output.receiveGetStoryEventDetailsResponse(response)
	}

	private fun responseModel(storyEvent: StoryEvent) =
	  GetStoryEventDetails.ResponseModel(
		storyEvent.id.uuid,
		storyEvent.name,
		storyEvent.linkedLocationId?.uuid,
		storyEvent.includedCharacterIds.map(Character.Id::uuid)
	  )

	private suspend fun getStoryEvent(storyEventId: UUID) =
	  (storyEventRepository.getStoryEventById(StoryEvent.Id(storyEventId))
		?: throw StoryEventDoesNotExist(storyEventId))
}