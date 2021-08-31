package com.soyle.stories.usecase.storyevent.getStoryEventDetails

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.usecase.storyevent.StoryEventDoesNotExist
import com.soyle.stories.usecase.storyevent.StoryEventRepository
import java.util.*

class GetStoryEventDetailsUseCase(
    private val storyEventRepository: StoryEventRepository
) : GetStoryEventDetails {

    override suspend fun invoke(storyEventId: StoryEvent.Id, output: GetStoryEventDetails.OutputPort) {

        val storyEvent = storyEventRepository.getStoryEventOrError(storyEventId)

        val response = responseModel(storyEvent)
        output.receiveGetStoryEventDetailsResponse(response)
    }

    private fun responseModel(storyEvent: StoryEvent) =
        GetStoryEventDetails.ResponseModel(
            storyEvent.id.uuid,
            storyEvent.name.value,
            storyEvent.linkedLocationId,
            storyEvent.includedCharacterIds
        )
}