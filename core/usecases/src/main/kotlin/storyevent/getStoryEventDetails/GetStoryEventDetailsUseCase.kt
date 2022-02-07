package com.soyle.stories.usecase.storyevent.getStoryEventDetails

import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.usecase.storyevent.StoryEventRepository

class GetStoryEventDetailsUseCase(
    private val storyEventRepository: StoryEventRepository
) : GetStoryEventDetails {

    override suspend fun invoke(storyEventId: StoryEvent.Id, output: GetStoryEventDetails.OutputPort) {

        val storyEvent = storyEventRepository.getStoryEventOrError(storyEventId)

        val response = responseModel(storyEvent)
        output.receiveGetStoryEventDetailsResponse(response)
    }

    private fun responseModel(storyEvent: StoryEvent) =
        StoryEventDetails(
            storyEvent.id,
            storyEvent.name.value,
            storyEvent.linkedLocationId?.let { StoryEventLocation(it, "") },
            storyEvent.involvedCharacters.map {
                StoryEventCharacter(it.id, it.name)
            }
        )
}