package com.soyle.stories.usecase.storyevent.getStoryEventDetails

import java.util.*

interface GetStoryEventDetails {

	suspend operator fun invoke(storyEventId: UUID, output: OutputPort)

	class ResponseModel(val storyEventId: UUID, val storyEventName: String, val locationId: UUID?, val includedCharacterIds: List<UUID>)

	interface OutputPort {

		fun receiveGetStoryEventDetailsFailure(failure: Exception)
		fun receiveGetStoryEventDetailsResponse(response: ResponseModel)
	}
}