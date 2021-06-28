package com.soyle.stories.storyevent.createStoryEvent

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.storyevent.createStoryEvent.CreateStoryEvent
import java.util.*

class CreateStoryEventControllerImpl(
  projectId: String,
  private val threadTransformer: ThreadTransformer,
  private val createStoryEvent: CreateStoryEvent,
  private val createStoryEventOutputPort: CreateStoryEvent.OutputPort
) : CreateStoryEventController {

	private val projectId = UUID.fromString(projectId)

	override fun createStoryEvent(name: NonBlankString) {
		createStoryEvent(CreateStoryEvent.RequestModel(name, projectId))
	}

	override fun createStoryEventBefore(name: NonBlankString, relativeStoryEventId: String) {
		val request = CreateStoryEvent.RequestModel.insertBefore(name, UUID.fromString(relativeStoryEventId))
		createStoryEvent(request)
	}

	override fun createStoryEventAfter(name: NonBlankString, relativeStoryEventId: String) {
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