package com.soyle.stories.storyevent.createStoryEvent

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.storyevent.usecases.createStoryEvent.CreateStoryEvent
import java.util.*

class CreateStoryEventControllerImpl(
  projectId: String,
  private val threadTransformer: ThreadTransformer,
  private val createStoryEvent: CreateStoryEvent,
  private val createStoryEventOutputPort: CreateStoryEvent.OutputPort
) : CreateStoryEventController {

	private val projectId = UUID.fromString(projectId)

	override fun createStoryEvent(name: String) {
		createStoryEvent(CreateStoryEvent.RequestModel(name, projectId))
	}

	override fun createStoryEventBefore(name: String, relativeStoryEventId: String) {
		val request = CreateStoryEvent.RequestModel.insertBefore(name, UUID.fromString(relativeStoryEventId))
		createStoryEvent(request)
	}

	override fun createStoryEventAfter(name: String, relativeStoryEventId: String) {
		val request = CreateStoryEvent.RequestModel.insertAfter(name, UUID.fromString(relativeStoryEventId))
		createStoryEvent(request)
	}

	private fun createStoryEvent(request: CreateStoryEvent.RequestModel)
	{
		threadTransformer.async {
			createStoryEvent.invoke(request, createStoryEventOutputPort)
		}
	}
}