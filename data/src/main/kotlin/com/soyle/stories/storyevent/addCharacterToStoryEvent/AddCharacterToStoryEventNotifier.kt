package com.soyle.stories.storyevent.addCharacterToStoryEvent

import com.soyle.stories.common.Notifier
import com.soyle.stories.storyevent.usecases.addCharacterToStoryEvent.AddCharacterToStoryEvent

class AddCharacterToStoryEventNotifier : Notifier<AddCharacterToStoryEvent.OutputPort>(), AddCharacterToStoryEvent.OutputPort {
	override fun receiveAddCharacterToStoryEventResponse(response: AddCharacterToStoryEvent.ResponseModel) {
		notifyAll { it.receiveAddCharacterToStoryEventResponse(response) }
	}

	override fun receiveAddCharacterToStoryEventFailure(failure: Exception) {
		notifyAll { it.receiveAddCharacterToStoryEventFailure(failure) }
	}
}