package com.soyle.stories.storyevent.usecases.getStoryEventDetails

import com.soyle.stories.storyevent.StoryEventException
import java.util.*

interface GetStoryEventDetails {

	suspend operator fun invoke(storyEventId: UUID, output: OutputPort)

	class ResponseModel(val storyEventId: UUID, val storyEventName: String, val locationId: UUID?, val includedCharacterIds: List<UUID>)

	interface OutputPort {

		fun receiveGetStoryEventDetailsFailure(failure: StoryEventException)
		fun receiveGetStoryEventDetailsResponse(response: ResponseModel)
	}
}