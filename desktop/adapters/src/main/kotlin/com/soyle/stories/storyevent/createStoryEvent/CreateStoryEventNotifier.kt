package com.soyle.stories.storyevent.createStoryEvent

import com.soyle.stories.common.Notifier
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.usecase.storyevent.createStoryEvent.CreateStoryEvent

class CreateStoryEventNotifier(
	private val threadTransformer: ThreadTransformer
) : CreateStoryEvent.OutputPort, Notifier<CreateStoryEvent.OutputPort>() {
	override fun receiveCreateStoryEventResponse(response: CreateStoryEvent.ResponseModel) {
		threadTransformer.async {
			notifyAll { it.receiveCreateStoryEventResponse(response) }
		}
	}

	override fun receiveCreateStoryEventFailure(failure: Exception) {
		threadTransformer.async {
			notifyAll { it.receiveCreateStoryEventFailure(failure) }
		}
	}
}