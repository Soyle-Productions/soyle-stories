package com.soyle.stories.storyevent.usecases.linkLocationToStoryEvent

import java.util.*

interface LinkLocationToStoryEvent {

	suspend operator fun invoke(storyEventId: UUID, locationId: UUID?, outputPort: OutputPort)

	class ResponseModel(val storyEventId: UUID, val locationId: UUID?, val unlinkedLocationId: UUID?)

	interface OutputPort {
		fun receiveLinkLocationToStoryEventFailure(failure: Exception)
		fun receiveLinkLocationToStoryEventResponse(response: ResponseModel)
	}

}