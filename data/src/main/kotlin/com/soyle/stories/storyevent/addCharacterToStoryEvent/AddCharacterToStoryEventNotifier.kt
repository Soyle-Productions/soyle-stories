package com.soyle.stories.storyevent.addCharacterToStoryEvent

import com.soyle.stories.common.Notifier
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.storyevent.usecases.addCharacterToStoryEvent.AddCharacterToStoryEvent

class AddCharacterToStoryEventNotifier(
	private val threadTransformer: ThreadTransformer
) : Notifier<AddCharacterToStoryEvent.OutputPort>(), AddCharacterToStoryEvent.OutputPort {
	override fun receiveAddCharacterToStoryEventResponse(response: AddCharacterToStoryEvent.ResponseModel) {
		threadTransformer.async {
			notifyAll { it.receiveAddCharacterToStoryEventResponse(response) }
		}
	}

	override fun receiveAddCharacterToStoryEventFailure(failure: Exception) {
		threadTransformer.async {
			notifyAll { it.receiveAddCharacterToStoryEventFailure(failure) }
		}
	}
}