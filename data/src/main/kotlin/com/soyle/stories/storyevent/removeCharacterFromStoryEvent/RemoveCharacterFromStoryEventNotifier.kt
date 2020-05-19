package com.soyle.stories.storyevent.removeCharacterFromStoryEvent

import com.soyle.stories.common.Notifier
import com.soyle.stories.storyevent.StoryEventException
import com.soyle.stories.storyevent.usecases.removeCharacterFromStoryEvent.RemoveCharacterFromStoryEvent

class RemoveCharacterFromStoryEventNotifier : Notifier<RemoveCharacterFromStoryEvent.OutputPort>(), RemoveCharacterFromStoryEvent.OutputPort {
	override fun receiveRemoveCharacterFromStoryEventResponse(response: RemoveCharacterFromStoryEvent.ResponseModel) {
		notifyAll { it.receiveRemoveCharacterFromStoryEventResponse(response) }
	}

	override fun receiveRemoveCharacterFromStoryEventFailure(failure: StoryEventException) {
		notifyAll { it.receiveRemoveCharacterFromStoryEventFailure(failure) }
	}
}