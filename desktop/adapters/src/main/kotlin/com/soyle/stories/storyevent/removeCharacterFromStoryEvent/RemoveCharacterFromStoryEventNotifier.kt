package com.soyle.stories.storyevent.removeCharacterFromStoryEvent

import com.soyle.stories.common.Notifier
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.usecase.storyevent.removeCharacterFromStoryEvent.RemoveCharacterFromStoryEvent

class RemoveCharacterFromStoryEventNotifier(
	private val threadTransformer: ThreadTransformer
) : Notifier<RemoveCharacterFromStoryEvent.OutputPort>(), RemoveCharacterFromStoryEvent.OutputPort {
	override fun receiveRemoveCharacterFromStoryEventResponse(response: RemoveCharacterFromStoryEvent.ResponseModel) {
		threadTransformer.async {
			notifyAll { it.receiveRemoveCharacterFromStoryEventResponse(response) }
		}
	}

	override fun receiveRemoveCharacterFromStoryEventFailure(failure: Exception) {
		threadTransformer.async {
			notifyAll { it.receiveRemoveCharacterFromStoryEventFailure(failure) }
		}
	}
}