package com.soyle.stories.storyevent.createStoryEvent

import com.soyle.stories.common.Notifier
import com.soyle.stories.storyevent.StoryEventException
import com.soyle.stories.storyevent.usecases.createStoryEvent.CreateStoryEvent

class CreateStoryEventNotifier : CreateStoryEvent.OutputPort, Notifier<CreateStoryEvent.OutputPort>() {
	override fun receiveCreateStoryEventResponse(response: CreateStoryEvent.ResponseModel) {
		notifyAll { it.receiveCreateStoryEventResponse(response) }
	}

	override fun receiveCreateStoryEventFailure(failure: StoryEventException) {
		notifyAll { it.receiveCreateStoryEventFailure(failure) }
	}
}