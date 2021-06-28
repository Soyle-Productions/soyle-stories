package com.soyle.stories.storyevent.removeCharacterFromStoryEvent

import com.soyle.stories.character.removeCharacterFromStory.RemovedCharacterReceiver
import com.soyle.stories.usecase.character.removeCharacterFromStory.RemoveCharacterFromStory
import com.soyle.stories.usecase.character.removeCharacterFromStory.RemovedCharacter
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.usecase.storyevent.removeCharacterFromStoryEvent.RemoveCharacterFromStoryEvent
import java.util.*

class RemoveCharacterFromStoryEventControllerImpl(
  private val threadTransformer: ThreadTransformer,
  private val removeCharacterFromStoryEvent: RemoveCharacterFromStoryEvent,
  private val removeCharacterFromStoryEventOutputPort: RemoveCharacterFromStoryEvent.OutputPort
) : RemoveCharacterFromStoryEventController, RemovedCharacterReceiver {
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

	override suspend fun receiveCharacterRemoved(characterRemoved: RemovedCharacter) {
		threadTransformer.async {
			removeCharacterFromStoryEvent.removeCharacterFromAllStoryEvents(
				characterRemoved.characterId,
				removeCharacterFromStoryEventOutputPort
			)
		}
	}

	private fun formatStoryEventId(storyEventId: String) = UUID.fromString(storyEventId)
	private fun formatCharacterId(characterId: String) = UUID.fromString(characterId)
}