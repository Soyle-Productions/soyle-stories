package com.soyle.stories.storyevent.usecases.createStoryEvent

import com.soyle.stories.storyevent.StoryEventException
import com.soyle.stories.storyevent.usecases.StoryEventItem
import java.util.*

interface CreateStoryEvent {

	class RequestModel private constructor(val name: String, val projectId: UUID?, val relativeStoryEventId: UUID?, val before: Boolean)
	{
		constructor(name: String, projectId: UUID) : this(name, projectId, null, false)
		companion object {
			fun insertBefore(name: String, relativeStoryEventId: UUID) = RequestModel(name, null, relativeStoryEventId, true)
			fun insertAfter(name: String, relativeStoryEventId: UUID) = RequestModel(name, null, relativeStoryEventId, false)
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
		fun receiveCreateStoryEventFailure(failure: StoryEventException)
		fun receiveCreateStoryEventResponse(response: ResponseModel)
	}

}