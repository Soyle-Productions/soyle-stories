package com.soyle.stories.usecase.storyevent.createStoryEvent

import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.storyevent.StoryEventItem
import java.util.*

interface CreateStoryEvent {

	class RequestModel private constructor(val name: NonBlankString, val projectId: UUID?, val relativeStoryEventId: UUID?, val before: Boolean)
	{
		constructor(name: NonBlankString, projectId: UUID) : this(name, projectId, null, false)
		companion object {
			fun insertBefore(name: NonBlankString, relativeStoryEventId: UUID) = RequestModel(name, null, relativeStoryEventId, true)
			fun insertAfter(name: NonBlankString, relativeStoryEventId: UUID) = RequestModel(name, null, relativeStoryEventId, false)
		}
	}

	suspend operator fun invoke(request: RequestModel, output: OutputPort)

	class ResponseModel(val newItem: StoryEventItem, val updatedStoryEvents: List<StoryEventItem>) {
		val storyEventId: UUID
			get() = newItem.storyEventId
		val storyEventName: String
			get() = newItem.storyEventName
		val influenceOrderIndex: Int
			get() = newItem.influenceOrderIndex

	}

	interface OutputPort
	{
		fun receiveCreateStoryEventFailure(failure: Exception)
		fun receiveCreateStoryEventResponse(response: ResponseModel)
	}

}