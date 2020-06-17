package com.soyle.stories.storyevent.removeCharacterFromStoryEvent

import com.soyle.stories.character.usecases.removeCharacterFromStory.RemoveCharacterFromStory
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.storyevent.usecases.removeCharacterFromStoryEvent.RemoveCharacterFromStoryEvent
import java.util.*

class RemoveCharacterFromStoryEventControllerImpl(
  private val threadTransformer: ThreadTransformer,
  private val removeCharacterFromStoryEvent: RemoveCharacterFromStoryEvent,
  private val removeCharacterFromStoryEventOutputPort: RemoveCharacterFromStoryEvent.OutputPort
) : RemoveCharacterFromStoryEventController, RemoveCharacterFromStory.OutputPort {
	override fun removeCharacter(storyEventId: String, characterId: String) {
		val formattedStoryEventId = formatStoryEventId(storyEventId)
		val formattedCharacterId = formatCharacterId(characterId)
		threadTransformer.async {
			removeCharacterFromStoryEvent.invoke(
			  formattedStoryEventId,
			  formattedCharacterId,
			  removeCharacterFromStoryEventOutputPort
			)
		}
	}

	override fun receiveRemoveCharacterFromStoryResponse(response: RemoveCharacterFromStory.ResponseModel) {
		threadTransformer.async {
			removeCharacterFromStoryEvent.removeCharacterFromAllStoryEvents(
				response.characterId,
				removeCharacterFromStoryEventOutputPort
			)
		}
	}

	private fun formatStoryEventId(storyEventId: String) = UUID.fromString(storyEventId)
	private fun formatCharacterId(characterId: String) = UUID.fromString(characterId)

	override fun receiveRemoveCharacterFromStoryFailure(failure: Exception) {}
}