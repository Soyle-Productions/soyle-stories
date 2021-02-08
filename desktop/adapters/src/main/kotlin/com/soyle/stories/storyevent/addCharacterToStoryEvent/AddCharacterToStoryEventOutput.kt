package com.soyle.stories.storyevent.addCharacterToStoryEvent

import com.soyle.stories.common.Notifier
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.usecase.storyevent.addCharacterToStoryEvent.AddCharacterToStoryEvent
import com.soyle.stories.usecase.storyevent.addCharacterToStoryEvent.IncludedCharacterInStoryEvent

class AddCharacterToStoryEventOutput(
	private val threadTransformer: ThreadTransformer,
	private val includedCharacterInStoryEventReceiver: IncludedCharacterInStoryEventReceiver
) : AddCharacterToStoryEvent.OutputPort {

	override fun receiveAddCharacterToStoryEventResponse(response: AddCharacterToStoryEvent.ResponseModel) {
		threadTransformer.async {
			includedCharacterInStoryEventReceiver.receiveIncludedCharacterInStoryEvent(
				IncludedCharacterInStoryEvent(response.storyEventId, response.characterId)
			)
		}
	}

	override fun receiveAddCharacterToStoryEventFailure(failure: Exception) = throw failure
}