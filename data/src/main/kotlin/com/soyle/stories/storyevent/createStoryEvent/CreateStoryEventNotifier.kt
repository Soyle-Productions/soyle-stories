package com.soyle.stories.storyevent.createStoryEvent

import com.soyle.stories.common.Notifier
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.storyevent.StoryEventException
import com.soyle.stories.storyevent.usecases.createStoryEvent.CreateStoryEvent

class CreateStoryEventNotifier(
	private val threadTransformer: ThreadTransformer
) : CreateStoryEvent.OutputPort, Notifier<CreateStoryEvent.OutputPort>() {
	override fun receiveCreateStoryEventResponse(response: CreateStoryEvent.ResponseModel) {
		threadTransformer.async {
			notifyAll { it.receiveCreateStoryEventResponse(response) }
		}
	}

	override fun receiveCreateStoryEventFailure(failure: StoryEventException) {
		threadTransformer.async {
			notifyAll { it.receiveCreateStoryEventFailure(failure) }
		}
	}
}