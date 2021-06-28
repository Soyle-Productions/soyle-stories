package com.soyle.stories.storyevent.addCharacterToStoryEvent

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.usecase.storyevent.addCharacterToStoryEvent.AddCharacterToStoryEvent
import java.util.*

class AddCharacterToStoryEventControllerImpl(
  private val threadTransformer: ThreadTransformer,
  private val addCharacterToStoryEvent: AddCharacterToStoryEvent,
  private val addCharacterToStoryEventOutputPort: AddCharacterToStoryEvent.OutputPort
) : AddCharacterToStoryEventController {

	override fun addCharacterToStoryEvent(storyEventId: String, characterId: String) {
		val formattedStoryEventId = formatStoryEventId(storyEventId)
		val formattedCharacterId = formatCharacterId(characterId)
		threadTransformer.async {
			addCharacterToStoryEvent.invoke(formattedStoryEventId, formattedCharacterId, addCharacterToStoryEventOutputPort)
		}
	}

	private fun formatStoryEventId(storyEventId: String) = UUID.fromString(storyEventId)
	private fun formatCharacterId(characterId: String) = UUID.fromString(characterId)
}