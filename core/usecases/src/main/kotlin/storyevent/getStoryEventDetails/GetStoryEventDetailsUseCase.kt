package com.soyle.stories.usecase.storyevent.getStoryEventDetails

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.usecase.storyevent.StoryEventDoesNotExist
import com.soyle.stories.usecase.storyevent.StoryEventRepository
import java.util.*

class GetStoryEventDetailsUseCase(
  private val storyEventRepository: StoryEventRepository
) : GetStoryEventDetails {

	override suspend fun invoke(storyEventId: UUID, output: GetStoryEventDetails.OutputPort) {
		val response = try {
			val storyEvent = getStoryEvent(storyEventId)

			responseModel(storyEvent)
		} catch (e: Exception) {
			return output.receiveGetStoryEventDetailsFailure(e)
		}
		output.receiveGetStoryEventDetailsResponse(response)
	}

	private fun responseModel(storyEvent: StoryEvent) =
		GetStoryEventDetails.ResponseModel(
			storyEvent.id.uuid,
			storyEvent.name.value,
            storyEvent.linkedLocationId?.uuid,
			storyEvent.includedCharacterIds.map(Character.Id::uuid)
		)

	private suspend fun getStoryEvent(storyEventId: UUID) =
	  (storyEventRepository.getStoryEventById(StoryEvent.Id(storyEventId))
		?: throw StoryEventDoesNotExist(storyEventId))
}